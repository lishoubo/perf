package com.personal.li.perf.client.model;

/**
 * Created by lishoubo on 16/4/16.
 */
public class Response<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Response<T> build(int code, String message, T data) {
        Response<T> tResponse = new Response<>();
        tResponse.code = code;
        tResponse.message = message;
        tResponse.data = data;
        return tResponse;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
