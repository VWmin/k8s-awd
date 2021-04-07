package com.vwmin.k8sawd.web.task;

import com.vwmin.k8sawd.web.entity.Team;
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
        KubernetesClient client = (KubernetesClient) jobDataMap.get("client");
        Integer competitionId = (Integer) jobDataMap.get("competitionId");
        List<Team> teams = ((TeamService) jobDataMap.get("teamService")).teamsByCompetition(competitionId);

        log.info("正在为比赛{}创建deploy，共计{}", competitionId, teams.size());


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
//                    .withNewType("NodePort") //指定宿主机上绑定端口
                    .withPorts(new ServicePortBuilder().withPort(80).withNewTargetPort(80).build())
//                    .withPorts(new ServicePortBuilder().withPort(80).withNewTargetPort(80).build())
                .endSpec()

                .build()
        );

        // 创建一个Ingress试试
        client.network().ingresses().create(new IngressBuilder()
                .withNewMetadata()
                    .withName(appName + "-ingress")
                    .addToAnnotations("nginx.ingress.kubernetes.io/rewrite-target", "/$2")
                .endMetadata()
                .withNewSpec()
                    .addNewRule()
                    .withNewHttp()
                    .addNewPath()
                    .withPath("/" + appName + "(/|$)(.*)").withNewBackend().withServiceName(appName + "-service").withServicePort(new IntOrString(80)).endBackend()
                    .endPath()
                    .endHttp()
                    .endRule()
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
