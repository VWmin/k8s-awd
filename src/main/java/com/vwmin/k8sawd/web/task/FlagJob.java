package com.vwmin.k8sawd.web.task;

import com.vwmin.k8sawd.web.entity.Flag;
import com.vwmin.k8sawd.web.service.FlagService;
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
import java.util.UUID;
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
        FlagService flagService = (FlagService) jobDataMap.get("flagService");
        Integer competitionId = (Integer) jobDataMap.get("competitionId");
        Integer teamId = (Integer) jobDataMap.get("teamId");

        try {
            newFlag(client, flagService, genAppName(competitionId, teamId), teamId);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            ie.printStackTrace();
        }
    }

    private void newFlag(KubernetesClient client, FlagService flagService,
                         String appName, int teamId) throws InterruptedException {



        log.info("正在等待Deployment[{}-deployment]准备完毕，将在2min后超时", appName);
        // 等待deployment准备好，如果超时会报错
        client.apps().deployments().withName(appName + "-deployment")
                .waitUntilReady(2, TimeUnit.MINUTES);
        log.info("{}-deployment准备完成", appName);


        // 通过label找到对应pod的name
        PodList podList = client.pods().withLabel("app", appName).list();
        assert podList.getItems().size() == 1;
        String podName = podList.getItems().get(0).getMetadata().getName();
        log.info("查询到Pod[{}]", podName);

        // 创建一个flag
        String flagVal = UUID.randomUUID().toString();
        log.info("预计写入flag[{}]", flagVal);

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
        } else{
            // 写入Pod成功后将flag写入数据库
            Flag flag = new Flag(flagVal, teamId);
            flagService.save(flag);
        }

        execWatch.close();
    }

    private String genAppName(int competitionId, int teamId) {
        return "awd" + "-" + competitionId + "-" + teamId;
    }

    @Slf4j
    private static class WriteFlagListener implements ExecListener {
        private final CountDownLatch execLatch;

        public WriteFlagListener(CountDownLatch execLatch){
            this.execLatch = execLatch;
        }

        @Override
        public void onOpen(Response response) {
            log.info("尝试向Pod写入flag，将在5s后超时");
        }

        @Override
        public void onFailure(Throwable t, Response response) {
            log.info("尝试执行命令时出错, msg: {}", t.getMessage());
            execLatch.countDown();
        }

        @Override
        public void onClose(int code, String reason) {
            log.info("执行完成, code: {}", code);
            execLatch.countDown();
        }
    }
}