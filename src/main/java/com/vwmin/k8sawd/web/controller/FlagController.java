package com.vwmin.k8sawd.web.controller;

import com.vwmin.k8sawd.web.aop.ExpectedStatus;
import com.vwmin.k8sawd.web.enums.CompetitionStatus;
import com.vwmin.k8sawd.web.model.CompetitionHandler;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.service.TeamService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/4 21:03
 */
@Slf4j
@RestController
public class FlagController {

    private final CompetitionHandler competitionHandler;
    private final TeamService teamService;


    public FlagController(CompetitionHandler competitionHandler, TeamService teamService) {
        this.competitionHandler = competitionHandler;
        this.teamService = teamService;
    }

    @PostMapping("/flag")
    @ExpectedStatus(expected = {CompetitionStatus.RUNNING})
    public ResponseEntity<Response> receiveFlag(@RequestHeader("Authorization") String token,
                                                @RequestBody FlagJson flag){
        competitionHandler.validFlag(teamService.getTeamByToken(token).getId(), flag.flag);
        return Response.success("提交成功");
    }

    @GetMapping("/flag")
    @ExpectedStatus(expected = {CompetitionStatus.RUNNING})
    public ResponseEntity<Response> getFlag(@RequestParam int teamId){
        return Response.success(competitionHandler.getFlagValByTeamId(teamId));
    }

    @Data
    private static class FlagJson {
        private String flag;
    }


}
