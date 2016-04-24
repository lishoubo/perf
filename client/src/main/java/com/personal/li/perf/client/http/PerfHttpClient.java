package com.personal.li.perf.client.http;

import com.personal.li.perf.client.model.Request;
import com.personal.li.perf.client.model.Response;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by lishoubo on 16/4/19.
 */
public class PerfHttpClient {
    private static class Holder {
        private static final PerfHttpClient INSTANCE = new PerfHttpClient();
    }

    private static Logger logger = LoggerFactory.getLogger(PerfHttpClient.class);
    private final CloseableHttpClient httpClient;

    private PoolingHttpClientConnectionManager connectionManager = null;
    private HttpClientBuilder clientBuilder = null;

    private int TIMEOUT = 500;
    private int MAX_CONNECTION = 3000;
    private int MAX_HOST_CONNECTION = 2000;

    public static PerfHttpClient instance() {
        return Holder.INSTANCE;
    }

    private PerfHttpClient() {
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(MAX_CONNECTION);
        connectionManager.setDefaultMaxPerRoute(MAX_HOST_CONNECTION);
        clientBuilder = HttpClients.custom();
        clientBuilder.setConnectionManager(connectionManager);
        clientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                boolean r = !(exception instanceof SocketTimeoutException) && super.retryRequest(exception, executionCount, context);
                logger.error("RetryRequest {} {}", r, exception.getMessage());
                return r;
            }
        });
        clientBuilder.disableRedirectHandling();
        httpClient = clientBuilder.build();

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                PoolStats stats = connectionManager.getTotalStats();
                StringBuilder sb = new StringBuilder("vapHttpClient ");
                sb.append(stats.getPending()).append(" ");
                sb.append(stats.getLeased()).append(" ");
                sb.append(stats.getAvailable()).append(" ");
                sb.append(stats.getMax());
                logger.info("[perf][pool] {}", sb.toString());
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    PoolingHttpClientConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public Response send(Request request) throws Exception {
        String method = request.getMethod();
        if ("GET".equalsIgnoreCase(method)) {
            return sendGet(request);
        }
        return sendPost(request);
    }

    private Response sendPost(Request request) throws Exception {
        String targetUrl = request.getUrl();
        HttpPost post = new HttpPost(targetUrl);
        if (request.getBody() != null && request.getBody().length > 0) {
            post.setEntity(new ByteArrayEntity(request.getBody()));
        }
        post.setHeader("Content-type", "application/json");
        return parse(execute(post));
    }

    private Response sendGet(Request request) throws Exception {
        String targetUrl = request.getUrl();
        HttpGet get = new HttpGet(targetUrl);
        get.setHeader("Content-type", "application/json");
        return parse(execute(get));
    }

    private Response parse(CloseableHttpResponse httpResponse) throws IOException {
        EntityUtils.consumeQuietly(httpResponse.getEntity());
        return Response.build(0, null, null);
    }

    public CloseableHttpResponse execute(final HttpRequestBase request) throws Exception {
        return execute(request, TIMEOUT);
    }

    public CloseableHttpResponse execute(final HttpRequestBase request, int timeout) throws Exception {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .build();
        request.setConfig(requestConfig);
        return httpClient.execute(request);
    }

}
