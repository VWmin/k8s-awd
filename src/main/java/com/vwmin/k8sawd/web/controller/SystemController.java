package com.vwmin.k8sawd.web.controller;

import cn.hutool.core.lang.Pair;
import com.vwmin.k8sawd.web.entity.Competition;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.service.CompetitionService;
import com.vwmin.k8sawd.web.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 15:33
 */
@RestController
@RequestMapping("/manager")
public class SystemController {

    private final CompetitionService competitionService;

    public SystemController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }


    @GetMapping("/system/runningCompetition")
    public ResponseEntity<Response> getRunningCompetition() {
        Pair<Boolean, Integer> pair = competitionService.runningCompetition();

        return pair.getKey()
                ? Response.success(competitionService.getById(pair.getValue()))
                : Response.success("没有找到正在进行中的比赛");
    }

    @DeleteMapping("/system/competition")
    public ResponseEntity<Response> finishAll() {

        competitionService.finishAll();

        return Response.success();
    }

}
