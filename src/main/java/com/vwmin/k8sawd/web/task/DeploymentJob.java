package com.vwmin.k8sawd.web.task;

import com.vwmin.k8sawd.web.entity.Team;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
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
        log.info("定时任务测试.");

        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        KubernetesClient client = (KubernetesClient) jobDataMap.get("client");

        @SuppressWarnings("unchecked")
        List<Team> teams = (List<Team>) jobDataMap.get("teams");

        Integer competitionId = (Integer) jobDataMap.get("competitionId");

        for (Team team : teams){
            deploy(client, genAppName(competitionId, team));
        }
    }

    private void deploy(KubernetesClient client, String appName){
        // 创建一个deployment试试
        client.apps().deployments().create(new DeploymentBuilder()
                .withNewMetadata()
                    .withName(appName + "-deployment")
                .endMetadata()
                .withNewSpec()
                    .withReplicas(1)
                    .withNewSelector()
                        .addToMatchLabels("app", appName)
                    .endSelector()
                    .withNewTemplate()
                        .withNewMetadata()
                            .addToLabels("app", appName)
                        .endMetadata()
                        .withNewSpec()
                            .addNewContainer()
                                .withName("awd-docker")
                                .withImage("awd:1.0")
                                .withPorts(new ContainerPortBuilder().withContainerPort(80).build())
                            .endContainer()
                        .endSpec()
                    .endTemplate()
                .endSpec()

                .build()
        );

        // 创建一个service试试
        client.services().create(new ServiceBuilder()
                .withNewMetadata()
                    .withName(appName + "-service")
                .endMetadata()
                .withNewSpec()
                    .addToSelector("app", appName)
                    .withNewType("NodePort") //指定宿主机上绑定端口
                    .withPorts(new ServicePortBuilder().withPort(80).withNewTargetPort(80).build())
//                    .withPorts(new ServicePortBuilder().withPort(80).withNewTargetPort(80).build())
                .endSpec()

                .build()
        );
    }

    /**
     * app标签的生成规则
     */
    private String genAppName(Integer competitionId, Team team){
        return "awd" + "-" + competitionId + "-" + team.getId();
    }
}
