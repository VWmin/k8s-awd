package com.vwmin.k8sawd.web.controller;

import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.service.LogService;
import com.vwmin.k8sawd.web.service.ManagerService;
import com.vwmin.k8sawd.web.service.SystemService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;


/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/16 14:28
 */
@Slf4j
@RestController
@RequestMapping("/manager")
public class DataPanelController {

    private final ManagerService managerService;
    private final LogService logService;
    private final SystemService systemService;

    public DataPanelController(ManagerService managerService, LogService logService, SystemService systemService) {
        this.managerService = managerService;
        this.logService = logService;
        this.systemService = systemService;
    }

    @GetMapping("/panel")
    public ResponseEntity<Response> panel(){
        Runtime runtime = Runtime.getRuntime();

        Panel panel = new Panel();
        panel.setSubmitFlag(0);
        panel.setCheckDown(0); // fixme
        panel.setNumGoroutine(0); // fixme
        panel.setMemAllocated((runtime.totalMemory() - runtime.freeMemory()) >> 10);
        panel.setTotalScore(0);
        panel.setPreviousRoundScore(0);
        panel.setVersion("0.0.1");
        panel.setCommitSHA("???");
        panel.setBuildTime("???");

        return Response.success(panel);
    }

    @GetMapping("/logs")
    public ResponseEntity<Response> logs(){
        return Response.success(logService.logs());
    }

    @GetMapping("/base")
    public ResponseEntity<Response> base(){
        return Response.success(new HashMap<String, String>(){{
            put("title", "FWY 8003117070");
            put("language", "zh-CN");
        }});
    }

    @Data
    private static class Panel {
        private int submitFlag;

        private int checkDown;

        // fixme: 原程序中是Go程数量，到时候换掉
        private int numGoroutine;

        private double memAllocated;

        private int totalScore;

        private int previousRoundScore;

        private String version;

        private String commitSHA;

        private String buildTime;

    }


}
