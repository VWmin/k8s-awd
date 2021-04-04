package com.vwmin.k8sawd.web.controller;

import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.service.FlagService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/4 21:03
 */
@Slf4j
@RestController
public class FlagController {

    private final FlagService flagService;


    public FlagController(FlagService flagService) {
        this.flagService = flagService;
    }

    @PostMapping("/flag")
    public ResponseEntity<Response> receiveFlag(@RequestBody FlagJson flag){

        log.info("flag: {}", flag.getFlag());

        return Response.success();
    }

    @Data
    private static class FlagJson {
        private String flag;
    }


}
