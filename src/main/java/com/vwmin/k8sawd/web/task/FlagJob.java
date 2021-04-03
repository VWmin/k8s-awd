package com.vwmin.k8sawd.web.task;

import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import io.fabric8.kubernetes.client.internal.readiness.Readiness;
import io.fabric8.kubernetes.client.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 以（比赛，队伍）为元组，定时向pod插入一条flag
 * @author vwmin
 * @version 1.0
 * @date 2021/4/3 20:35
 */
@Slf4j
public class FlagJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        KubernetesClient client = (KubernetesClient) jobDataMap.get("client");
        Integer competitionId = (Integer) jobDataMap.get("competitionId");
        Integer teamId = (Integer) jobDataMap.get("teamId");

        try {
            newFlag(client, genAppName(competitionId, teamId));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            ie.printStackTrace();
        }
    }

    private void newFlag(KubernetesClient client, String appName) throws InterruptedException {



        // 等待deployment准备好，如果超时会报错
        client.apps().deployments().withName(appName + "-deployment")
                .waitUntilReady(2, TimeUnit.MINUTES);


        // 通过label找到对应pod的name
        PodList podList = client.pods().withLabel("app", appName).list();
        assert podList.getItems().size() == 1;
        String podName = podList.getItems().get(0).getMetadata().getName();

        // 在pod内执行一个命令
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream error = new ByteArrayOutputStream();
        ExecWatch execWatch = client.pods().withName(podName)
                .writingOutput(out)
                .writingError(error)
                .usingListener(new WriteFlagListener())
                .exec("ls", "/");

        // 等待执行结束
        final CountDownLatch execLatch = new CountDownLatch(1);
        boolean latchTerminationStatus = execLatch.await(5, TimeUnit.SECONDS);
        if (!latchTerminationStatus) {
            log.warn("Latch could not terminate within specified time");
        }

        // 打印执行结果到日志
        log.info("Exec Output: {} ", out);
        execWatch.close();
    }

    private String genAppName(int competitionId, int teamId) {
        return "awd" + "-" + competitionId + "-" + teamId;
    }

    @Slf4j
    private static class WriteFlagListener implements ExecListener {

        @Override
        public void onOpen(Response response) {

        }

        @Override
        public void onFailure(Throwable t, Response response) {

        }

        @Override
        public void onClose(int code, String reason) {

        }
    }
}
