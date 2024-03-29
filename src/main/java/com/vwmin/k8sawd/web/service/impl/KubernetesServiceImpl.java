package com.vwmin.k8sawd.web.service.impl;

import com.vwmin.k8sawd.web.entity.Image;
import com.vwmin.k8sawd.web.enums.LogKind;
import com.vwmin.k8sawd.web.enums.LogLevel;
import com.vwmin.k8sawd.web.model.CompetitionHandler;
import com.vwmin.k8sawd.web.service.KubernetesService;
import com.vwmin.k8sawd.web.service.LogService;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.networking.v1beta1.IngressBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/8 16:35
 */
@Slf4j
@Service
public class KubernetesServiceImpl implements KubernetesService {

    private final KubernetesClient client;
    private final LogService logService;

    public KubernetesServiceImpl(KubernetesClient client, LogService logService) {
        this.client = client;
        this.logService = logService;
    }

    @Override
    public boolean clearResource() {
        return client.apps().deployments().delete() &&
                client.services().delete() &&
                client.network().ingresses().delete();
    }

    @Override
    public String serviceEntry(int competitionId, int teamId) {
        String appName = nameRule(competitionId, teamId);
        return "http://121.36.230.118:30232/deployment/" + appName + "/";
    }

    @Override
    public String sshEntry(int competitionId, int teamId){
        String appName = nameRule(competitionId, teamId);
        return client.services()
                .withName(appName + "-ssh-service")
                .getURL(appName + "-ssh-port").substring(6);
    }

    @Override
    public void deploy(int competitionId, int teamId, Image image) {
        String appName = nameRule(competitionId, teamId);
        runSingle(appName, image.getName(), image.getPort(), image.isEnableSsh());
    }

    @Override
    public void writeFlag(CompetitionHandler competitionHandler, int competitionId, int teamId) throws InterruptedException {
        String appName = nameRule(competitionId, teamId);

        log.trace("正在等待Deployment[{}-deployment]准备完毕，将在2min后超时", appName);
        // 等待deployment准备好，如果超时会报错
        client.apps().deployments().withName(appName + "-deployment")
                .waitUntilReady(2, TimeUnit.MINUTES);
        log.trace("{}-deployment准备完成", appName);


        // 通过label找到对应pod的name
        PodList podList = client.pods().withLabel("app", appName).list();
        assert podList.getItems().size() == 1;
        String podName = podList.getItems().get(0).getMetadata().getName();
        log.trace("查询到Pod[{}]", podName);

        // 创建一个flag
        String flagVal = UUID.randomUUID().toString();
        log.trace("{}预计写入flag[{}]", appName, flagVal);

        // 计时器
        final CountDownLatch execLatch = new CountDownLatch(1);

        // 在pod内执行一个命令
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream error = new ByteArrayOutputStream();
        ExecWatch execWatch = client.pods().withName(podName)
                .writingOutput(out)
                .writingError(error)
                .usingListener(new WriteFlagListener(execLatch))
                .exec("/bin/bash", "-c", "echo " + flagVal + " > /flag.txt ");


        // 等待执行结束
        boolean latchTerminationStatus = execLatch.await(5, TimeUnit.SECONDS);
        if (!latchTerminationStatus) {
            // 执行失败
            log.warn("已超时，写入未能在指定时间内结束. err: {}", error);
        } else {
            // 写入Pod成功后向competitionHandler更新队伍flag
            competitionHandler.updateFlag(teamId, flagVal);
        }

        execWatch.close();
    }

    @Override
    public void demo(Image image) {
        stopSingle("awd-demo");
        runSingle("awd-demo", image.getName(), image.getPort(), image.isEnableSsh());
    }

    @Override
    public void stopDemo() {
        stopSingle("awd-demo");
    }

    @Slf4j
    private static class WriteFlagListener implements ExecListener {
        private final CountDownLatch execLatch;

        public WriteFlagListener(CountDownLatch execLatch) {
            this.execLatch = execLatch;
        }

        @Override
        public void onOpen(Response response) {
            log.trace("尝试向Pod写入flag，将在5s后超时");
        }

        @Override
        public void onFailure(Throwable t, Response response) {
            log.warn("尝试执行命令时出错, msg: {}", t.getMessage());
            execLatch.countDown();
        }

        @Override
        public void onClose(int code, String reason) {
            log.trace("执行完成, code: {}", code);
            execLatch.countDown();
        }
    }

    private void runSingle(String appName, String imageName, int targetPort, boolean enableSsh) {
        // 创建一个deployment
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
                .withName(appName + "-docker")
                .withImage(imageName)
                .withPorts(new ContainerPortBuilder().withContainerPort(targetPort).build())
                .endContainer()
                .endSpec()
                .endTemplate()
                .endSpec()

                .build()
        );

        // 创建一个web-service
        client.services().create(new ServiceBuilder()
                        .withNewMetadata()
                        .withName(appName + "-web-service")
                        .endMetadata()
                        .withNewSpec()
                        .addToSelector("app", appName)
                        .withPorts(new ServicePortBuilder().withName(appName + "-web-port").withPort(80).withNewTargetPort(targetPort).build())
                        .endSpec()
                        .build()
        );

        // 创建一个ssh-service
        if (enableSsh){
            client.services().create(new ServiceBuilder()
                    .withNewMetadata()
                    .withName(appName + "-ssh-service")
                    .endMetadata()
                    .withNewSpec()
                    .addToSelector("app", appName)
                    .withNewType("NodePort")
                    .withPorts(new ServicePortBuilder().withName(appName + "-ssh-port").withPort(22).withNewTargetPort(22).build())
                    .endSpec()
                    .build()
            );
        }

        // 创建一个Ingress
        client.network().ingresses().create(new IngressBuilder()
                .withNewMetadata()
                .withName(appName + "-ingress")
                .addToAnnotations("nginx.ingress.kubernetes.io/rewrite-target", "/$2")
                .endMetadata()
                .withNewSpec()
                .addNewRule()
                .withNewHttp()
                .addNewPath()
                .withPath("/deployment/" + appName + "(/|$)(.*)").withNewBackend().withServiceName(appName + "-web-service").withServicePort(new IntOrString(80)).endBackend()
                .endPath()
                .endHttp()
                .endRule()
                .endSpec()

                .build()
        );

        logService.log(LogLevel.NORMAL, LogKind.SYSTEM,
                "Pod[%s:%s]已创建", appName, "http://121.36.230.118:30232/deployment/" + appName + "/");
    }

    private void stopSingle(String appName) {
        client.apps().deployments().withName(appName + "-deployment").delete();
        client.services().withName(appName + "-service").delete();
        client.network().ingresses().withName(appName + "-ingress").delete();
    }

    private String nameRule(int competitionId, int teamId) {
        return "awd" + "-" + competitionId + "-" + teamId;
    }
}
