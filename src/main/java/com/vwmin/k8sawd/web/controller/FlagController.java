package com.vwmin.k8sawd.web.controller;

import com.vwmin.k8sawd.web.model.CompetitionHandler;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.service.FlagService;
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


    public FlagController(CompetitionHandler competitionHandler) {
        this.competitionHandler = competitionHandler;
    }

    @PostMapping("/flag")
    public ResponseEntity<Response> receiveFlag(@RequestBody FlagJson flag){
        return Response.success(competitionHandler.validFlag(flag.teamId, flag.flag));
    }

    @GetMapping("/flag")
    public ResponseEntity<Response> getFlag(@RequestParam int teamId){
        return Response.success(competitionHandler.getFlagByTeamId(teamId));
    }

    @Data
    private static class FlagJson {
        private int teamId;
        private String flag;
    }


}
