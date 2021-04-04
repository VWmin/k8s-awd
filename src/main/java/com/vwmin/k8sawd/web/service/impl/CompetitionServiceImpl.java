package com.vwmin.k8sawd.web.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vwmin.k8sawd.web.entity.Competition;
import com.vwmin.k8sawd.web.entity.Team;
import com.vwmin.k8sawd.web.exception.RoutineException;
import com.vwmin.k8sawd.web.mapper.CompetitionMapper;
import com.vwmin.k8sawd.web.model.ResponseCode;
import com.vwmin.k8sawd.web.service.CompetitionService;
import com.vwmin.k8sawd.web.service.FlagService;
import com.vwmin.k8sawd.web.service.SystemService;
import com.vwmin.k8sawd.web.service.TeamService;
import com.vwmin.k8sawd.web.task.DeploymentJob;
import com.vwmin.k8sawd.web.task.FlagJob;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 11:45
 */
@Slf4j
@Service
public class CompetitionServiceImpl extends ServiceImpl<CompetitionMapper, Competition> implements CompetitionService {

    private final SystemService systemService;
    private final TeamService teamService;
    private final FlagService flagService;
    private final Scheduler scheduler;
    private final KubernetesClient client;

    public CompetitionServiceImpl(SystemService systemService, TeamService teamService, FlagService flagService,
                                  Scheduler scheduler, KubernetesClient client) {
        this.systemService = systemService;
        this.teamService = teamService;
        this.flagService = flagService;
        this.scheduler = scheduler;
        this.client = client;
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


        List<Team> teams = teamService.list();

        // 设置启动比赛的定时任务
        setDeploymentTask(competition, teams);

        // 设置更新flag的定时任务
        setFlagTask(competition, teams);


    }

    private void setFlagTask(Competition competition, List<Team> teams) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(FlagJob.class).build();
        job.getJobDataMap().put("client", client);
        job.getJobDataMap().put("flagService", flagService);
        job.getJobDataMap().put("competitionId", competition.getId());
        job.getJobDataMap().put("teams", teams);

        SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .startAt(localDateTime2Date(LocalDateTimeUtil.now().plusSeconds(5)))
                .endAt(localDateTime2Date(competition.getEndTime()))
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInMinutes(1)
                                .repeatForever()
                ).build();

        scheduler.scheduleJob(job, trigger);
    }

    private void setDeploymentTask(Competition competition, List<Team> teams) throws SchedulerException {
        JobDetail job = JobBuilder.newJob(DeploymentJob.class).build();
        // 传入k8s命令环境
        job.getJobDataMap().put("client", client);
        // 传入要启动的队伍信息
        job.getJobDataMap().put("teams", teams);
        // 传入当前比赛id
        job.getJobDataMap().put("competitionId", competition.getId());

        SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .startAt(localDateTime2Date(LocalDateTimeUtil.now().plusSeconds(5)))
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(0)
                                .withRepeatCount(0)
                ).build();

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
