package com.vwmin.k8sawd.web.controller;

import com.vwmin.k8sawd.web.entity.Competition;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.service.CompetitionService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Response> create(@RequestBody Competition competition) throws SchedulerException {
        log.info("{}", competition);

        competitionService.createCompetition(competition);

        return Response.success();
    }
}
