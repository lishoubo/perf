package com.personal.li.perf.client.http;

import com.personal.li.perf.client.MidGenerator;
import com.personal.li.perf.client.Monitor;
import com.personal.li.perf.client.Sender;
import com.personal.li.perf.client.model.Request;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lishoubo on 16/4/16.
 */
public class HttpSyncSender implements Sender {
    private static Logger logger = LoggerFactory.getLogger(HttpSyncSender.class);
    private static Logger access = LoggerFactory.getLogger("access");

    private Monitor monitor;
    private Request request;

    static {
        System.setProperty("http.keepAlive", "true");
        System.setProperty("http.maxConnections", "100");
    }

    public HttpSyncSender(Monitor monitor, Request request) {
        this.monitor = monitor;
        this.request = request;
    }

    @Override
    public void send() {
        HttpURLConnection httpURLConnection;
        long begin = System.currentTimeMillis();
        String mid = MidGenerator.nextMid();
        try {
            httpURLConnection = doSend(mid);
            monitor.recordQps();
        } catch (Exception e) {
            logger.error("[client]", e);
            return;
        }
        try {
            parseResult(httpURLConnection);
        } catch (IOException e) {
            logger.error("[client]", e);
            monitor.recordError();
        }
        long duration = System.currentTimeMillis() - begin;
        access.info("[client] mid:{}, duration:{}", mid, duration);
        monitor.recordTps(duration);
    }

    private void parseResult(HttpURLConnection connection) throws IOException {
        int status = connection.getResponseCode();
        if (status != 200) {
            monitor.recordError();
        }
        InputStream inputStream = connection.getInputStream();
        IOUtils.closeQuietly(inputStream);
    }

    private HttpURLConnection doSend(String mid) throws IOException {
        URL realUrl = new URL(request.getUrl());
        HttpURLConnection connection = open(realUrl);
        setHeaders(connection, mid);

        connection.setDoInput(true);

        String method = request.getMethod();
        if ("GET".equalsIgnoreCase(method)) {
            connection.setRequestMethod("GET");
            connection.setDoOutput(false);
            connection.connect();
            return connection;
        }
        connection.setRequestMethod("POST");

        final boolean hasBody = (request.getBody() != null && request.getBody().length > 0);
        connection.setDoOutput(hasBody);
        connection.connect();
        if (hasBody) {
            sendBody(connection, request);
        }
        return connection;
    }

    private void setHeaders(HttpURLConnection connection, String mid) {
        connection.setRequestProperty("Content-type", "application/json");
        connection.setRequestProperty("mid", mid);
        connection.setRequestProperty("X-begin", String.valueOf(System.currentTimeMillis()));
    }

    private void sendBody(HttpURLConnection connection, Request request) throws IOException {
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(request.getBody());
        outputStream.flush();
        IOUtils.closeQuietly(outputStream);
    }

    private HttpURLConnection open(URL realUrl) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) realUrl.openConnection();
        httpURLConnection.setConnectTimeout(1000);
        httpURLConnection.setReadTimeout(5000);
        httpURLConnection.setInstanceFollowRedirects(false);
        return httpURLConnection;
    }
}
