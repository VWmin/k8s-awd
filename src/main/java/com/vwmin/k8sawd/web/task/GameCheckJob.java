package com.vwmin.k8sawd.web.task;

import com.vwmin.k8sawd.web.service.KubernetesService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 做一些比赛结束的清理
 * @author vwmin
 * @version 1.0
 * @date 2021/4/8 15:42
 */
public class GameCheckJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        KubernetesService kubernetesService = (KubernetesService) jobDataMap.get("kubernetesService");

        kubernetesService.clearResource();
    }
}
