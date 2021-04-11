package com.vwmin.k8sawd.web.task;

import com.vwmin.k8sawd.web.entity.Flag;
import com.vwmin.k8sawd.web.entity.Team;
import com.vwmin.k8sawd.web.model.CompetitionHandler;
import com.vwmin.k8sawd.web.service.FlagService;
import com.vwmin.k8sawd.web.service.KubernetesService;
import com.vwmin.k8sawd.web.service.TeamService;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 以（比赛，队伍）为元组，定时向pod插入一条flag
 *
 * @author vwmin
 * @version 1.0
 * @date 2021/4/3 20:35
 */
@Slf4j
public class FlagJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        KubernetesService kubernetesService = (KubernetesService) jobDataMap.get("kubernetesService");
        CompetitionHandler competitionHandler = (CompetitionHandler) jobDataMap.get("competitionHandler");
        Integer competitionId = (Integer) jobDataMap.get("competitionId");
        List<Team> teams = ((TeamService) jobDataMap.get("teamService")).teamsByCompetition(competitionId);

        // 记录并清理原本的flag
        competitionHandler.roundCheck();

        try {
            // 获得对map的锁
            for (Team team : teams) {
                kubernetesService.writeFlag(competitionHandler, competitionId, team.getId());
            }
            // 释放对map的锁
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            ie.printStackTrace();
        }
    }
}
