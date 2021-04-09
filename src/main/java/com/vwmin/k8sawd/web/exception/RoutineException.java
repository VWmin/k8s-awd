package com.vwmin.k8sawd.web.exception;

import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.model.ResponseCode;
import org.springframework.http.ResponseEntity;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 17:10
 */
public class RoutineException extends RuntimeException{
    private final ResponseEntity<Response> response;

    public RoutineException(String msg){
        this(ResponseCode.FAIL, msg, null);
    }

    public RoutineException(ResponseCode exceptionCode){
        this(exceptionCode, "", null);
    }

    public RoutineException(ResponseCode exceptionCode, String additionalMsg){
        this(exceptionCode, additionalMsg, null);
    }

    public RoutineException(ResponseCode exceptionCode, Throwable throwable){
        this(exceptionCode, "", throwable);
    }

    public RoutineException(ResponseCode exceptionCode, String additionalMsg, Throwable throwable){
        super(exceptionCode.getMsg() + additionalMsg);
        if (throwable != null) {
            initCause(throwable);
        }
        this.response = Response.error(exceptionCode, additionalMsg);

    }


    public ResponseEntity<Response> getResponse() {
        return response;
    }
}
