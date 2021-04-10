package com.vwmin.k8sawd.web.controller.player;

import com.vwmin.k8sawd.web.entity.Team;
import com.vwmin.k8sawd.web.model.CompetitionHandler;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.service.KubernetesService;
import com.vwmin.k8sawd.web.service.TeamService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/10 13:16
 */
@RestController
public class PlayerController {

    private final TeamService teamService;
    private final KubernetesService kubernetesService;
    private final CompetitionHandler competitionHandler;

    public PlayerController(TeamService teamService, KubernetesService kubernetesService,
                            CompetitionHandler competitionHandler) {
        this.teamService = teamService;
        this.kubernetesService = kubernetesService;
        this.competitionHandler = competitionHandler;
    }


    @GetMapping("/team/info")
    public ResponseEntity<Response> info(@RequestHeader("Authorization") String token) {
        Team teamByToken = teamService.getTeamByToken(token);
        return Response.success(teamByToken);
    }

    @GetMapping("/team/gameboxes")
    public ResponseEntity<Response> service(@RequestHeader("Authorization") String token) {
        Team teamByToken = teamService.getTeamByToken(token);
        return Response.success(new ArrayList<CompetitionHandler.GameBox>() {{
            add(competitionHandler.gameBoxByTeamId(teamByToken.getId()));
        }});
    }

    @GetMapping("/team/gameboxes/all")
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
}
