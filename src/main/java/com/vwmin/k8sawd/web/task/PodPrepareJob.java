package com.vwmin.k8sawd.web.task;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/30 11:05
 */
@Slf4j
public class PodPrepareJob implements Job {


    @Override
    public void execute(JobExecutionContext context) {
        log.info("定时任务测试.");

        KubernetesClient client = (KubernetesClient) context.getJobDetail().getJobDataMap().get("client");

        // 创建一个deployment试试
        client.apps().deployments().create(new DeploymentBuilder()
                .withNewMetadata()
                    .withName("test-deployment")
                .endMetadata()
                .withNewSpec()
                    .withReplicas(1)
                    .withNewSelector()
                        .addToMatchLabels("app", "test")
                    .endSelector()
                    .withNewTemplate()
                        .withNewMetadata()
                            .addToLabels("app", "test")
                        .endMetadata()
                        .withNewSpec()
                            .addNewContainer()
                                .withName("nginx")
                                .withImage("nginx")
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
                    .withName("test-service")
                .endMetadata()
                .withNewSpec()
                    .addToSelector("app", "test")
                    .withNewType("NodePort")
                    .withPorts(new ServicePortBuilder().withPort(80).withNewTargetPort(80).withNodePort(32333).build())
                .endSpec()

                .build()
        );
    }
}
