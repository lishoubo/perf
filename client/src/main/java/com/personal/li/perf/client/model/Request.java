package com.personal.li.perf.client.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lishoubo on 16/4/16.
 */
public class Request {
    private String url;
    private String method;
    private byte[] body;
    private Map<String, String> headers;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
