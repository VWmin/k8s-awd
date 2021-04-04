package com.vwmin.k8sawd.web.controller;

import com.vwmin.k8sawd.web.entity.Team;
import com.vwmin.k8sawd.web.model.Response;
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
    private final CompetitionService competitionService;
    private final TeamService teamService;

    public PodController(KubernetesClient kubernetesClient, CompetitionService competitionService, TeamService teamService) {
        this.kubernetesClient = kubernetesClient;
        this.competitionService = competitionService;
        this.teamService = teamService;
    }

    @GetMapping("/pods")
    public ResponseEntity<Response> pods() {

        return Response.success(kubernetesClient.pods().list());
    }

    @GetMapping("/clear")
    public ResponseEntity<Response> clear() {

        return Response.success(kubernetesClient.apps().deployments().delete() &&
                kubernetesClient.services().delete());
    }

    @GetMapping("/services")
    public ResponseEntity<Response> services() {
        int competitionId = competitionService.runningCompetition();
        if (competitionId == -1){
            // 没有正在进行的比赛
            return Response.error();
        }
        List<Team> teams = teamService.list();

        Map<String, String> ret = new HashMap<>(teams.size());

        for (Team team : teams) {
            String serviceName = "awd-" + competitionId + "-" + team.getId() + "-service";
            String serviceEntry = kubernetesClient.services().withName(serviceName).getURL("service-entry");
            ret.put("队伍" + team.getId() + "服务入口", serviceEntry);
        }

        return Response.success(ret);
    }

}
