package com.vwmin.k8sawd.web.exception;

import com.vwmin.k8sawd.web.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 17:11
 */
@Slf4j
@ControllerAdvice
public class RoutineExceptionAdvice {

    @ResponseBody
    @ExceptionHandler({RoutineException.class})
    public ResponseEntity<Response> handleAjaxException(RoutineException exception) {
        log.warn(exception.getMessage());
//        exception.printStackTrace();
        return exception.getResponse();
    }
}
