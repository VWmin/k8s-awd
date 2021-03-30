package com.vwmin.k8sawd.web.task;

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
    public void execute(JobExecutionContext jobExecutionContext) {
        log.info("定时任务测试.");
    }
}
