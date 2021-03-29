package com.vwmin.k8sawd.web.model;

import org.springframework.http.ResponseEntity;

import java.util.HashMap;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 15:47
 */
public class Response extends HashMap<String, Object> {
    /**
     * 状态码
     */
    public static final String CODE_TAG = "error";

    /**
     * 返回内容
     */
    public static final String MSG_TAG = "msg";

    /**
     * 数据对象
     */
    public static final String DATA_TAG = "data";

    public Response(ResponseCode code, Object data, String msg) {
        super.put(CODE_TAG, code.value());
        super.put(DATA_TAG, data);
        super.put(MSG_TAG, msg);
    }

    public static ResponseEntity<Response> error(ResponseCode code) {
        return new ResponseEntity<>(new Response(code, null, code.getMsg()), code.getHttpStatus());
    }

    public static ResponseEntity<Response> error(ResponseCode code, String additionalMsg) {
        return new ResponseEntity<>(new Response(code, null, code.getMsg() + additionalMsg), code.getHttpStatus());
    }

    public static ResponseEntity<Response> success(Object data) {
        ResponseCode success = ResponseCode.SUCCESS;
        return new ResponseEntity<>(new Response(success, data, success.getMsg()), success.getHttpStatus());
    }

    public static ResponseEntity<Response> success() {
        return success("ok");
    }

    public static ResponseEntity<Response> error(){
        ResponseCode fail = ResponseCode.FAIL;
        return new ResponseEntity<>(new Response(fail, null, fail.getMsg()), fail.getHttpStatus());
    }
}
