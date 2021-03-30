package com.vwmin.k8sawd.web.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vwmin.k8sawd.web.entity.Competition;
import com.vwmin.k8sawd.web.exception.RoutineException;
import com.vwmin.k8sawd.web.mapper.CompetitionMapper;
import com.vwmin.k8sawd.web.model.ResponseCode;
import com.vwmin.k8sawd.web.service.CompetitionService;
import com.vwmin.k8sawd.web.service.SystemService;
import com.vwmin.k8sawd.web.task.PodPrepareJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 11:45
 */
@Service
public class CompetitionServiceImpl extends ServiceImpl<CompetitionMapper, Competition> implements CompetitionService {

    private final SystemService systemService;
    private final Scheduler scheduler;

    public CompetitionServiceImpl(SystemService systemService, Scheduler scheduler) {
        this.systemService = systemService;
        this.scheduler = scheduler;
    }

    @Override
    public void createCompetition(Competition competition) throws SchedulerException {
        // 如果有正在进行的比赛，则创建失败
        if (systemService.hasAlive()) {
            throw new RoutineException(ResponseCode.FAIL, "已有正在进行的比赛");
        }

        // 检查起止时间适合符合语义
//        checkTime(competition.getStartTime(), competition.getEndTime());

        // 写入记录，并设置为alive
        save(competition);
//        systemService.setCompetition(competition);

        // 设置定时任务

        JobDetail job = JobBuilder.newJob(PodPrepareJob.class).build();

        SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .startAt(localDateTime2Date(competition.getStartTime()))
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(0)
                                .withRepeatCount(0))
                .build();

        scheduler.scheduleJob(job, trigger);

    }

    private void checkTime(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime nowTime = LocalDateTime.now();

        // 开始时间至少在当前时间 1h 后
        Duration fromNow = LocalDateTimeUtil.between(nowTime, startTime);
        if (fromNow.toHours() < 1) {
            throw new RoutineException(ResponseCode.FAIL, "开始时间至少在当前时间 1h 后");
        }

        // 结束时间至少在开始时间后
        if (!endTime.isAfter(startTime)) {
            throw new RoutineException(ResponseCode.FAIL, "结束时间至少在开始时间后");
        }
    }


    private Date localDateTime2Date(LocalDateTime localDateTime) {
        return DateUtil.parse(DateUtil.formatLocalDateTime(localDateTime));
    }


}
