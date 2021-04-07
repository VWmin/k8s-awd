package com.vwmin.k8sawd.web.controller;

import com.vwmin.k8sawd.web.entity.Team;
import com.vwmin.k8sawd.web.model.CompetitionHandler;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.model.ResponseCode;
import com.vwmin.k8sawd.web.service.CompetitionService;
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

    private final KubernetesClient kubernetesClient;
    private final CompetitionHandler competitionHandler;
    private final TeamService teamService;

    public PodController(KubernetesClient kubernetesClient, CompetitionHandler competitionHandler, TeamService teamService) {
        this.kubernetesClient = kubernetesClient;
        this.competitionHandler = competitionHandler;
        this.teamService = teamService;
    }

    @GetMapping("/pods")
    public ResponseEntity<Response> pods() {

        return Response.success(kubernetesClient.pods().list());
    }

    @GetMapping("/clear")
    public ResponseEntity<Response> clear() {

        return Response.success(
                kubernetesClient.apps().deployments().delete() &&
                kubernetesClient.services().delete() &&
                kubernetesClient.network().ingresses().delete()
        );
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
            String appName = "awd-" + competitionId + "-" + team.getId();

            String entry = kubernetesClient.network().ingresses().withName(appName + "-ingress").isReady()
                    ? "http://121.36.230.118:30232/" + appName + "/"
                    : "";

            ret.put("队伍" + team.getId() + "服务入口", entry);
        }

        return Response.success(ret);
    }

}
