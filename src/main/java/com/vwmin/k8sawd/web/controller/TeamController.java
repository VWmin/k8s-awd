package com.vwmin.k8sawd.web.controller;

import com.vwmin.k8sawd.web.entity.Team;
import com.vwmin.k8sawd.web.model.CompetitionHandler;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 20:38
 */
@Slf4j
@RestController
@RequestMapping("/manager")
public class TeamController {
    private final
    TeamService teamService;

    // fixme: 创建team时的competitionId应由前端给出
    private final CompetitionHandler competitionHandler;

    public TeamController(TeamService teamService, CompetitionHandler competitionHandler) {
        this.teamService = teamService;
        this.competitionHandler = competitionHandler;
    }


    @PostMapping("/team")
    public ResponseEntity<Response> addTeam(@RequestBody Team team) {

        log.info("addTeam: {}", team);
        team.setCompetitionId(competitionHandler.getId());
        teamService.addTeam(team);

        return Response.success();
    }


    @DeleteMapping("/team")
    public ResponseEntity<Response> deleteTeam(@RequestParam("id") int id) {

        log.info("deleteTeam: {}", id);
        teamService.removeById(id);

        return Response.success();
    }

    @PutMapping("/team")
    public ResponseEntity<Response> editTeam(@RequestBody Team team) {

        log.info("editTeam: {}", team);
        teamService.editTeam(team);

        return Response.success();
    }

    @GetMapping("/teams")
    public ResponseEntity<Response> teams() {

        return Response.success(teamService.teamsByCompetition(competitionHandler.getId()));
    }


    @GetMapping("/team/rank")
    public ResponseEntity<Response> rank(){
        List<Team> teams = teamService.teamsByCompetition(competitionHandler.getId());
        teams.sort(Comparator.comparing(Team::getScore).reversed());

        return Response.success(teams);
    }
}
