package com.vwmin.k8sawd.web.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vwmin.k8sawd.web.entity.Competition;
import com.vwmin.k8sawd.web.exception.RoutineException;
import com.vwmin.k8sawd.web.mapper.CompetitionMapper;
import com.vwmin.k8sawd.web.model.CompetitionHandler;
import com.vwmin.k8sawd.web.model.ResponseCode;
import com.vwmin.k8sawd.web.service.*;
import com.vwmin.k8sawd.web.task.DeploymentJob;
import com.vwmin.k8sawd.web.task.FlagJob;
import com.vwmin.k8sawd.web.task.GameCheckJob;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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
    private final Scheduler scheduler;
    private final KubernetesService kubernetesService;
    private final CompetitionHandler competitionHandler;


    // fixme: 比赛创建后一分钟启动比赛，测试用，正式时应当删除
    private LocalDateTime startAt;

    public CompetitionServiceImpl(SystemService systemService, TeamService teamService,
                                  Scheduler scheduler, KubernetesService kubernetesService, CompetitionHandler competitionHandler) {
        this.systemService = systemService;
        this.teamService = teamService;
        this.scheduler = scheduler;
        this.kubernetesService = kubernetesService;
        this.competitionHandler = competitionHandler;

    }

    @PostConstruct
    public void init() {
        int id = systemService.runningCompetition();
        if (id != -1) {
            competitionHandler.setRunningCompetition(getById(id));
        }
    }

    @Override
    public void createCompetition(Competition competition) throws SchedulerException {
        // 如果有已设置的比赛，则创建失败
        if (competitionHandler.isSet() || competitionHandler.isRunning()) {
            throw new RoutineException(ResponseCode.FAIL, "已存在一个比赛，考虑删除后再试");
        }

        // 检查起止时间适合符合语义
        checkTime(competition.getStartTime(), competition.getEndTime());

        // 写入记录，并设置为alive
        save(competition);
        systemService.setCompetition(competition);
        competitionHandler.setRunningCompetition(competition);

        // 对于新的比赛，清理先前比赛的队伍数据
        teamService.removeAll();

        startAt = LocalDateTimeUtil.now().plusMinutes(1);


        // 设置启动比赛的定时任务
        setDeploymentTask();

        // 设置更新flag的定时任务
        setFlagTask();

        // 设置比赛结束的定时任务
        setGameCheckTask();

    }

    private void setGameCheckTask() throws SchedulerException {
        Competition competition = competitionHandler.getRunningCompetition();
        JobDetail job = JobBuilder.newJob(GameCheckJob.class).withIdentity("gameCheckJob").build();
        job.getJobDataMap().put("kubernetesService", kubernetesService);

        SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .startAt(localDateTime2Date(competition.getEndTime()))
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(0)
                                .withRepeatCount(0)
                ).build();

        scheduler.scheduleJob(job, trigger);
    }


    private void setFlagTask() throws SchedulerException {
        Competition competition = competitionHandler.getRunningCompetition();
        JobDetail job = JobBuilder.newJob(FlagJob.class).withIdentity("flagJob").build();
        job.getJobDataMap().put("kubernetesService", kubernetesService);
        job.getJobDataMap().put("competitionHandler", competitionHandler);
        job.getJobDataMap().put("competitionId", competition.getId());
        job.getJobDataMap().put("teamService", teamService);

        SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .startAt(localDateTime2Date(competition.getStartTime()))
                .endAt(localDateTime2Date(competition.getEndTime()))
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInMinutes(1)
                                .repeatForever()
                ).build();

        scheduler.scheduleJob(job, trigger);
    }

    private void setDeploymentTask() throws SchedulerException {
        Competition competition = competitionHandler.getRunningCompetition();
        JobDetail job = JobBuilder.newJob(DeploymentJob.class).withIdentity("deploymentJob").build();
        // 传入k8s命令环境
        job.getJobDataMap().put("kubernetesService", kubernetesService);
        // 传入要启动的队伍信息
        job.getJobDataMap().put("teamService", teamService);
        // 传入当前比赛id
        job.getJobDataMap().put("competitionId", competition.getId());

        SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .startAt(localDateTime2Date(competition.getStartTime()))
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(0)
                                .withRepeatCount(0)
                ).build();

        scheduler.scheduleJob(job, trigger);
    }

    @Override
    public List<Competition> list() {
        List<Competition> list = super.list();
        int current = competitionHandler.isUnset() ? -1 : competitionHandler.getId();
        for (Competition competition : list) {
            boolean isAlive = competition.getId() == current && !competitionHandler.isFinished();
            String status = !isAlive ? "已结束" : (competitionHandler.isRunning() ? "正在进行" : "等待开始");
            competition.setStatus(status);
            competition.setAlive(isAlive);
        }
        return list;
    }

    private void checkTime(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime nowTime = LocalDateTime.now();

        // 开始时间至少在当前时间 1h 后
        Duration fromNow = LocalDateTimeUtil.between(nowTime, startTime);
        if (fromNow.toMinutes() < 1) {
            throw new RoutineException(ResponseCode.FAIL, "开始时间至少在当前时间 1min 后");
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
