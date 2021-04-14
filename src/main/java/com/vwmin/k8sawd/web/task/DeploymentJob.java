package com.vwmin.k8sawd.web.task;

import com.vwmin.k8sawd.web.entity.Image;
import com.vwmin.k8sawd.web.entity.Team;
import com.vwmin.k8sawd.web.service.KubernetesService;
import com.vwmin.k8sawd.web.service.TeamService;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.networking.v1beta1.Ingress;
import io.fabric8.kubernetes.api.model.networking.v1beta1.IngressBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.List;

/**
 * 创建一个由（比赛，队伍）决定的运行环境
 * @author vwmin
 * @version 1.0
 * @date 2021/3/30 11:05
 */
@Slf4j
public class DeploymentJob implements Job {


    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        KubernetesService kubernetesService = (KubernetesService) jobDataMap.get("kubernetesService");
        Integer competitionId = (Integer) jobDataMap.get("competitionId");
        List<Team> teams = ((TeamService) jobDataMap.get("teamService")).teamsByCompetition(competitionId);
        Image image = (Image) jobDataMap.get("image");

        log.info("正在为比赛{}创建deploy，共计{}", competitionId, teams.size());


        for (Team team : teams){
            kubernetesService.deploy(competitionId, team.getId(), image);
        }
    }
}
