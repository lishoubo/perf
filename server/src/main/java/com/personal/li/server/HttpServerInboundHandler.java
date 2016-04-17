package com.personal.li.server;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lishoubo on 16/4/16.
 */
public class HttpServerInboundHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(HttpServerInboundHandler.class);
    private static Logger access = LoggerFactory.getLogger("access");

    private String mid;
    private long begin, clientBegin;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            if (!request.headers().get(HttpHeaders.Names.CONTENT_TYPE).equals("application/json")) {
                sendResponse(ctx, -1, "not support content type.", null);
                return;
            }
            if (!request.getMethod().equals(HttpMethod.POST)) {
                sendResponse(ctx, -1, "not support request method.", null);
                return;
            }
            mid = request.headers().get("mid");
            begin = System.currentTimeMillis();
            clientBegin = Long.parseLong(request.headers().get("X-begin"));
            return;
        }
        if (msg instanceof DefaultLastHttpContent) {
            try {
                handleRequest(ctx, (DefaultLastHttpContent) msg);
            } catch (Throwable e) {
                logger.error("[server]", e);
                sendResponse(ctx, -1, "error", null);
                return;
            }
            access.info("[server] mid:{}, consume:{}", mid, (System.currentTimeMillis() - begin));
        }
    }

    private void handleRequest(ChannelHandlerContext ctx, DefaultLastHttpContent msg) {
        DefaultLastHttpContent httpContent = msg;
        byte[] bytes = new byte[httpContent.content().readableBytes()];
        httpContent.content().readBytes(bytes);
        String md5 = DigestUtils.md5Hex(bytes);
        sendResponse(ctx, 0, null, md5);
    }

    private void sendResponse(ChannelHandlerContext ctx, int code, String msg, String data) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(JSON.toJSONString(Result.result(code, msg, data)).getBytes()));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/json");
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        response.headers().set("mid", mid);
        response.headers().set("X-Client-Begin", clientBegin);
        response.headers().set("X-Server-cost", (System.currentTimeMillis() - begin));
        ctx.write(response);
        ctx.flush();
    }

}
