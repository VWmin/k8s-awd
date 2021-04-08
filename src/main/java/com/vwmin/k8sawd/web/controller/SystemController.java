package com.vwmin.k8sawd.web.controller;

import com.vwmin.k8sawd.web.exception.RoutineException;
import com.vwmin.k8sawd.web.model.CompetitionHandler;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.model.ResponseCode;
import com.vwmin.k8sawd.web.service.CompetitionService;
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

    private final CompetitionHandler competitionHandler;

    public SystemController(CompetitionHandler competitionHandler) {
        this.competitionHandler = competitionHandler;
    }


    @GetMapping("/system/runningCompetition")
    public ResponseEntity<Response> getRunningCompetition() {
        if (!competitionHandler.isSet()){
            throw new RoutineException(ResponseCode.FAIL, "没有比赛被创建");
        }
        return Response.success(competitionHandler.getRunningCompetition());
    }

    @DeleteMapping("/system/competition")
    public ResponseEntity<Response> finishAll() {

        competitionHandler.finishAll();

        return Response.success();
    }

}
