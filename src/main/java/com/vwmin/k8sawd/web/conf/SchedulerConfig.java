package com.vwmin.k8sawd.web.conf;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/30 10:46
 */
@Configuration
public class SchedulerConfig {

    @Bean
    public Scheduler deploymentScheduler() throws SchedulerException {
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        return scheduler;
    }

}
