package com.personal.li.server;

/**
 * Created by lishoubo on 16/4/16.
 */
public class Result<T> {
    private int code;
    private String message;

    private T data;

    public static <T> Result result(int code, String message, T data) {
        Result<T> tResult = new Result<>();
        tResult.setData(data);
        tResult.setMessage(message);
        return tResult;
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
