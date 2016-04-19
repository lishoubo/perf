package com.personal.li.perf.client.http;

import com.personal.li.perf.client.Monitor;
import com.personal.li.perf.client.Sender;
import com.personal.li.perf.client.model.Request;
import com.personal.li.perf.client.model.Response;
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
public class HttpSyncClientSender implements Sender {
    private static Logger logger = LoggerFactory.getLogger(HttpSyncClientSender.class);
    private static Logger access = LoggerFactory.getLogger("access");

    private Monitor monitor;
    private Request request;

    public HttpSyncClientSender(Monitor monitor, Request request) {
        this.monitor = monitor;
        this.request = request;
    }

    @Override
    public void send() {
        long begin = System.currentTimeMillis();

        Response response = null;
        try {
            response = PerfHttpClient.instance().send(request);
        } catch (Exception e) {
            logger.error("[sync][apache]", e);
        }
        if (response == null || response.getCode() != 0) {
            monitor.recordError();
        }
        final long duration = System.currentTimeMillis() - begin;
        monitor.recordTps(duration);
    }
}
