package com.vwmin.k8sawd.web.controller;

import com.vwmin.k8sawd.web.exception.RoutineException;
import com.vwmin.k8sawd.web.model.CompetitionHandler;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.model.ResponseCode;
import com.vwmin.k8sawd.web.service.CompetitionService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/manager")
public class SystemController {

    private final CompetitionHandler competitionHandler;

    public SystemController(CompetitionHandler competitionHandler) {
        this.competitionHandler = competitionHandler;
    }


    @GetMapping("/system/runningCompetition")
    public ResponseEntity<Response> getRunningCompetition() {
        if (competitionHandler.isUnset() || competitionHandler.isFinished()){
            throw new RoutineException(ResponseCode.FAIL, "没有比赛被创建");
        }
        return Response.success(competitionHandler.getRunningCompetition());
    }

    @GetMapping("/competition/status")
    public ResponseEntity<Response> competitionStatus(){
        return Response.success(competitionHandler.status());
    }

    @DeleteMapping("/system/competition")
    public ResponseEntity<Response> finishAll() throws SchedulerException {

        competitionHandler.finishAll();

        return Response.success();
    }

}
