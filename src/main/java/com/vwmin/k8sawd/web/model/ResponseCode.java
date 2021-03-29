package com.vwmin.k8sawd.web.model;

import org.springframework.http.HttpStatus;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 15:49
 */
public enum  ResponseCode {
    SUCCESS(0, HttpStatus.OK,"ok."),

    FAIL(1, HttpStatus.INTERNAL_SERVER_ERROR, "failed.");


    private final int code;

    private final HttpStatus status;

    private final String msg;

    ResponseCode(int code, HttpStatus status, String msg) {
        this.code = code;
        this.status = status;
        this.msg = msg;
    }

    public int value() {
        return this.code;
    }

    public HttpStatus getHttpStatus(){
        return status;
    }

    public String getMsg(){
        return msg;
    }
}
