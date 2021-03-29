package com.vwmin.k8sawd.web.controller;

import cn.hutool.core.lang.Pair;
import com.vwmin.k8sawd.web.entity.Competition;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.service.SystemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 15:33
 */
@RestController
@RequestMapping("/manager/system")
public class SystemController {

    private final SystemService systemService;

    public SystemController(SystemService systemService) {
        this.systemService = systemService;
    }


    @GetMapping("/runningCompetition")
    public ResponseEntity<Response> getRunningCompetition() {
        Pair<Boolean, Competition> pair = systemService.runningCompetition();
        if (pair.getKey()) {
            return Response.success(pair.getValue());
        } else {
            return Response.success("没有找到正在进行中的比赛");
        }
    }

}
