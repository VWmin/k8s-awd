package com.vwmin.k8sawd.web.controller;

import com.vwmin.k8sawd.web.aop.ExpectedStatus;
import com.vwmin.k8sawd.web.entity.Team;
import com.vwmin.k8sawd.web.enums.CompetitionStatus;
import com.vwmin.k8sawd.web.enums.LogKind;
import com.vwmin.k8sawd.web.enums.LogLevel;
import com.vwmin.k8sawd.web.model.CompetitionHandler;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.service.LogService;
import com.vwmin.k8sawd.web.service.TeamService;
import com.vwmin.k8sawd.web.service.UploadLogoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final TeamService teamService;
    private final UploadLogoService uploadLogoService;
    private final LogService logService;
    private final CompetitionHandler competitionHandler;

    public TeamController(TeamService teamService, UploadLogoService uploadLogoService, LogService logService, CompetitionHandler competitionHandler) {
        this.teamService = teamService;
        this.uploadLogoService = uploadLogoService;
        this.logService = logService;
        this.competitionHandler = competitionHandler;
    }


    @PostMapping("/team")
    @ExpectedStatus(expected = {CompetitionStatus.SET})
    public ResponseEntity<Response> addTeam(@RequestBody Team team) {

        teamService.addTeam(team, competitionHandler.getId());
        logService.log(LogLevel.NORMAL, LogKind.MANAGER_OPERATE,
                "队伍[%s]创建成功", team.getName()
        );
        return Response.success();
    }

    @PostMapping("/teams")
    @ExpectedStatus(expected = {CompetitionStatus.SET})
    public ResponseEntity<Response> addTeams(@RequestBody List<Team> teams) {
        teamService.addTeams(teams, competitionHandler.getId());

        StringBuilder logTeamNames = new StringBuilder();
        teams.stream().map(Team::getName).forEach(v -> logTeamNames.append("[").append(v).append("] "));

        logService.log(LogLevel.NORMAL, LogKind.MANAGER_OPERATE,
                "队伍%s创建成功，共计：%d", logTeamNames.toString(), teams.size()
        );

        return Response.success(teams);
    }


    @DeleteMapping("/team")
    @ExpectedStatus(expected = {CompetitionStatus.SET})
    public ResponseEntity<Response> deleteTeam(@RequestParam("id") int id) {

        Team team = teamService.getById(id);
        teamService.removeById(id);
        logService.log(LogLevel.NORMAL, LogKind.MANAGER_OPERATE,
                "队伍[%s]已删除.", team.getName()
        );

        return Response.success("队伍删除成功");
    }

    @PutMapping("/team")
    @ExpectedStatus(expected = {CompetitionStatus.SET})
    public ResponseEntity<Response> editTeam(@RequestBody Team team) {


        teamService.editTeam(team);

        return Response.success();
    }

    @PostMapping("/team/resetPassword")
    @ExpectedStatus(expected = {CompetitionStatus.SET})
    public ResponseEntity<Response> resetPass(@RequestBody Team team) {

        teamService.resetPassword(team);

        logService.log(LogLevel.NORMAL, LogKind.MANAGER_OPERATE,
                "队伍[%s]登录密码已重置", team.getName());


        return Response.success(team.getPassword());
    }

    @GetMapping("/teams")
    @ExpectedStatus(expected = {CompetitionStatus.SET, CompetitionStatus.RUNNING, CompetitionStatus.FINISHED})
    public ResponseEntity<Response> teams() {

        return Response.success(teamService.teamsByCompetition(competitionHandler.getId()));
    }


    @GetMapping("/team/rank")
    @ExpectedStatus(expected = {CompetitionStatus.SET, CompetitionStatus.RUNNING, CompetitionStatus.FINISHED})
    public ResponseEntity<Response> rank(){
        List<Team> teams = teamService.teamsByCompetition(competitionHandler.getId());
        teams.sort(Comparator.comparing(Team::getScore).reversed());

        return Response.success(teams);
    }

    @PostMapping("/team/uploadLogo")
    @ExpectedStatus(expected = {CompetitionStatus.SET})
    public ResponseEntity<Response> uploadPicture(@RequestParam MultipartFile picture){
        return Response.success(uploadLogoService.checkAndSave(picture));
    }
}
