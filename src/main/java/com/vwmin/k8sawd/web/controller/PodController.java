package com.vwmin.k8sawd.web.controller;

import com.vwmin.k8sawd.web.entity.Team;
import com.vwmin.k8sawd.web.model.CompetitionHandler;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.model.ResponseCode;
import com.vwmin.k8sawd.web.service.KubernetesService;
import com.vwmin.k8sawd.web.service.TeamService;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/31 12:08
 */
@RestController
@RequestMapping("/manager")
public class PodController {

    private final KubernetesService kubernetesService;
    private final CompetitionHandler competitionHandler;
    private final TeamService teamService;

    public PodController(KubernetesService kubernetesService, CompetitionHandler competitionHandler, TeamService teamService) {
        this.kubernetesService = kubernetesService;
        this.competitionHandler = competitionHandler;
        this.teamService = teamService;
    }


    @GetMapping("/clear")
    public ResponseEntity<Response> clear() {
        return Response.success(kubernetesService.clearResource());
    }

    @GetMapping("/services")
    public ResponseEntity<Response> services() {
        if (!competitionHandler.isRunning()){
            // 没有正在进行的比赛
            return Response.error(ResponseCode.FAIL, "没有正在进行的比赛");
        }
        int competitionId = competitionHandler.getRunningCompetition().getId();
        List<Team> teams = teamService.teamsByCompetition(competitionId);

        Map<String, String> ret = new HashMap<>(teams.size());

        for (Team team : teams) {
            String entry = kubernetesService.serviceEntry(competitionId, team.getId());
            ret.put("队伍" + team.getId() + "服务入口", entry);
        }

        return Response.success(ret);
    }

}
