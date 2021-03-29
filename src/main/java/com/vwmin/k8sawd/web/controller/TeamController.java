package com.vwmin.k8sawd.web.controller;

import com.vwmin.k8sawd.web.entity.Team;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }


    @PostMapping("/team")
    public ResponseEntity<Response> addTeam(@RequestBody Team team) {

        log.info("addTeam: {}", team);
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

        return Response.success(teamService.list());
    }
}
