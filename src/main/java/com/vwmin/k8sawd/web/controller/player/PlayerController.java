package com.vwmin.k8sawd.web.controller.player;

import com.vwmin.k8sawd.web.aop.ExpectedStatus;
import com.vwmin.k8sawd.web.entity.Team;
import com.vwmin.k8sawd.web.enums.CompetitionStatus;
import com.vwmin.k8sawd.web.model.CompetitionHandler;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.service.BulletinService;
import com.vwmin.k8sawd.web.service.ImageService;
import com.vwmin.k8sawd.web.service.TeamService;
import lombok.Data;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/10 13:16
 */
@RestController
public class PlayerController {

    private final TeamService teamService;
    private final CompetitionHandler competitionHandler;
    private final BulletinService bulletinService;
    private final ImageService imageService;

    public PlayerController(TeamService teamService,
                            CompetitionHandler competitionHandler, BulletinService bulletinService, ImageService imageService) {
        this.teamService = teamService;
        this.competitionHandler = competitionHandler;
        this.bulletinService = bulletinService;
        this.imageService = imageService;
    }


    @GetMapping("/team/info")
    @ExpectedStatus(expected = {CompetitionStatus.SET, CompetitionStatus.RUNNING, CompetitionStatus.FINISHED})
    public ResponseEntity<Response> info(@RequestHeader("Authorization") String token) {
        Team teamByToken = teamService.getTeamByToken(token);
        return Response.success(teamByToken);
    }

    @GetMapping("/team/gameboxes")
    @ExpectedStatus(expected = {CompetitionStatus.RUNNING})
    public ResponseEntity<Response> service(@RequestHeader("Authorization") String token) {
        Team teamByToken = teamService.getTeamByToken(token);
        return Response.success(new ArrayList<CompetitionHandler.GameBox>() {{
            add(competitionHandler.gameBoxByTeamId(teamByToken.getId()));
            if (imageService.image().isEnableSsh()) {
                add(competitionHandler.sshEntryByTeamId(teamByToken.getId()));
            }
        }});
    }

    @GetMapping("/team/gameboxes/all")
    @ExpectedStatus(expected = {CompetitionStatus.RUNNING})
    public ResponseEntity<Response> services(@RequestHeader("Authorization") String token) {
        Team teamByToken = teamService.getTeamByToken(token);
        Integer competitionId = teamByToken.getCompetitionId();
        List<Team> teams = teamService.teamsByCompetition(competitionId);
        List<CompetitionHandler.GameBox> ret = new ArrayList<>(teams.size());

        for (Team team : teams) {
            CompetitionHandler.GameBox gameBox = competitionHandler.gameBoxByTeamId(team.getId());
            ret.add(gameBox);
        }

        return Response.success(ret);
    }


    @GetMapping("/team/rank")
    @ExpectedStatus(expected = {CompetitionStatus.RUNNING, CompetitionStatus.FINISHED})
    public ResponseEntity<Response> rank() {
        List<Team> teams = teamService.list();
        teams.sort(Comparator.comparing(Team::getScore).reversed());
        List<RankItem> rankList = new ArrayList<>(teams.size());
        for (Team team : teams) {
            boolean attacked = competitionHandler.isAttacked(team.getId());
            RankItem item = new RankItem(team, attacked);
            rankList.add(item);
        }
        return Response.success(new HashMap<String, Object>(){{
            put("title", new ArrayList<String>(){{add(competitionHandler.getTitle());}});
            put("rank", rankList);
        }});
    }

    @Data
    private static class RankItem {
        int teamId;
        String teamName;
        String teamLogo;
        int score;
        boolean isAttacked;

        RankItem(Team team, boolean isAttacked) {
            this.teamId = team.getId();
            this.teamName = team.getName();
            this.teamLogo = team.getLogo();
            this.score = team.getScore();
            this.isAttacked = isAttacked;
        }
    }


    @GetMapping("/time")
    public ResponseEntity<Response> getTime() {
        return Response.success(competitionHandler.getRound());
    }


    @GetMapping("/livelog")
    @ExpectedStatus(expected = {CompetitionStatus.SET, CompetitionStatus.RUNNING, CompetitionStatus.FINISHED})
    public SseEmitter livelog(@RequestParam String token) {
        SseEmitter sseEmitter = new SseEmitter(0L);
        competitionHandler.setSseEmitter(token, sseEmitter);
        return sseEmitter;
    }

    @GetMapping("/bulletins")
    @ExpectedStatus(expected = {CompetitionStatus.SET, CompetitionStatus.RUNNING, CompetitionStatus.FINISHED})
    public ResponseEntity<Response> retrieve() {
        return Response.success(bulletinService.list());
    }


}
