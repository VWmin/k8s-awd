package com.vwmin.k8sawd.web.controller;

import com.vwmin.k8sawd.web.aop.ExpectedStatus;
import com.vwmin.k8sawd.web.entity.Competition;
import com.vwmin.k8sawd.web.enums.CompetitionStatus;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.service.CompetitionService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 16:50
 */
@Slf4j
@RestController
@RequestMapping("/manager")
public class CompetitionController {
    private final CompetitionService competitionService;

    public CompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }


    @PostMapping("/competition")
    @ExpectedStatus(expected = {CompetitionStatus.UNSET, CompetitionStatus.FINISHED})
    public ResponseEntity<Response> create(@RequestBody Competition competition) throws SchedulerException {
        log.info("{}", competition);

        competitionService.createCompetition(competition);

        return Response.success("比赛创建成功");
    }

    @GetMapping("/competitions")
    public ResponseEntity<Response> competitions(){
        return Response.success(competitionService.list());
    }

}
