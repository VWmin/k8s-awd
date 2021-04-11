package com.vwmin.k8sawd.web.model;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.vwmin.k8sawd.web.entity.Competition;
import com.vwmin.k8sawd.web.entity.Flag;
import com.vwmin.k8sawd.web.entity.Team;
import com.vwmin.k8sawd.web.enums.CompetitionStatus;
import com.vwmin.k8sawd.web.exception.RoutineException;
import com.vwmin.k8sawd.web.service.FlagService;
import com.vwmin.k8sawd.web.service.KubernetesService;
import com.vwmin.k8sawd.web.service.SystemService;
import com.vwmin.k8sawd.web.service.TeamService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/6 19:24
 */
@Slf4j
@Component
public class CompetitionHandler {

    // 需要被CompetitionService初始化
    private Competition runningCompetition;
    private final ConcurrentHashMap<String, Flag> flagMap;
    private final FlagService flagService;
    private final TeamService teamService;
    private final SystemService systemService;
    private final KubernetesService kubernetesService;
    private final Scheduler scheduler;
    private SseEmitter sseEmitter = null;

    private CompetitionStatus status = CompetitionStatus.UNSET;


    public CompetitionHandler(FlagService flagService, TeamService teamService,
                              SystemService systemService, KubernetesService kubernetesService, Scheduler scheduler) {
        this.flagService = flagService;
        this.teamService = teamService;
        this.systemService = systemService;
        this.kubernetesService = kubernetesService;
        this.scheduler = scheduler;
        runningCompetition = null;
        flagMap = new ConcurrentHashMap<>();
    }

    public Competition getRunningCompetition() {
        return this.runningCompetition;
    }

    public int getId() {
        return this.runningCompetition.getId();
    }

    public void setRunningCompetition(Competition runningCompetition) {
        this.runningCompetition = runningCompetition;
        this.status = CompetitionStatus.SET;
    }

    public CompetitionStatus status() {
        return this.status;
    }

    private void start() {
        this.status = CompetitionStatus.RUNNING;
    }

    public boolean isUnset() {
        return this.status == CompetitionStatus.UNSET;
    }

    public boolean isSet() {
        return this.status == CompetitionStatus.SET;
    }

    public boolean isRunning() {
        return this.status == CompetitionStatus.RUNNING;
    }

    public boolean isFinished() {
        return this.status == CompetitionStatus.FINISHED;
    }

    /**
     * 统计上轮得分
     * 将所有队伍的flag设为expired，并写入数据库
     */
    public void roundCheck() {
        start();
        if (!flagMap.isEmpty()) {
            statistic();
            flushFlag();
        }
    }

    private void statistic() {
        Map<Integer, List<Flag>> collect = flagMap.values().stream().filter(Flag::isUsed)
                .collect(Collectors.groupingBy(Flag::getUsedBy));
        int baseScore = runningCompetition.getScore();
        collect.forEach((k, v) -> {
            int plusScore = baseScore * v.size();

            // 向数据库写入得分
            Team team = teamService.getById(k);
            team.plusScore(plusScore);
            teamService.updateById(team);

            log.info("队伍{}，本轮得分：{}", k, plusScore);
        });
    }

    public void flushFlag() {
        Collection<Flag> values = flagMap.values();
        flagService.saveBatch(values);
        flagMap.clear();
    }

    /**
     * 更新属于该队伍的flag
     *
     * @param flagVal flag
     * @param teamId  team
     */
    public void updateFlag(int teamId, String flagVal) {
        flagMap.put(flagVal, new Flag(teamId, flagVal));
    }

    /**
     * 1. 提交的flag不能是自己队伍的flag
     * 2. 提价的flag不能是expired或used
     * 3. 提交成功后flag标记为used
     *
     * @param teamId  提交该flag的队伍
     * @param flagVal 提交的flag
     */
    public void validFlag(int teamId, String flagVal) {
        if (!isRunning()) {
            throw new RoutineException("比赛未在进行中");
        }
        // 检查是不是一个flag
        if (!flagMap.containsKey(flagVal)) {
            throw new RoutineException("这不是一个合法的flag");
        }

        Flag flag = flagMap.get(flagVal);
        // 检查是否已使用或是自己队伍的flag
        if (flag.isUsed()) {
            throw new RoutineException("该flag已被使用");
        } else if (flag.getBelongTo().equals(teamId)) {
            throw new RoutineException("不能使用自己的flag");
        } else {
            flag.setUsed(true);
            flag.setUsedBy(teamId);

            try {
                if (sseEmitter != null) {
                    sseEmitter.send(LiveLogEvent.AttackEvent(teamId + "", flag.getBelongTo() + "", getTitle()));
                    sseEmitter.complete();
                }
            } catch (IOException ie) {
                ie.printStackTrace();
                sseEmitter.completeWithError(ie);
            }
        }
    }

    public void finishAll() throws SchedulerException {
        roundCheck();
        teamService.removeAll();
        scheduler.clear();
        kubernetesService.clearResource();
        systemService.finishAll();
        this.sseEmitter = null;
        this.status = CompetitionStatus.FINISHED;
    }

    @PreDestroy
    public void exit() throws SchedulerException {
        log.info("exiting...");
        finishAll();
    }

    public GameBox gameBoxByTeamId(int teamId) {
        if (!isRunning()) {
            return null;
        }
        Flag flag = getFlagByTeamId(teamId);

        String title = runningCompetition.getTitle();
        String entry = kubernetesService.serviceEntry(getId(), teamId);
        return new GameBox(title, teamService.getById(teamId).getName(),
                entry, runningCompetition.getScore(), flag.isUsed(), "暂无描述");
    }

    public boolean isAttacked(int teamId) {
        return getFlagByTeamId(teamId).isUsed();
    }

    public String getFlagValByTeamId(int teamId) {
        Optional<Flag> first = flagMap.values().stream()
                .filter(e -> e.getBelongTo().equals(teamId))
                .findFirst();
        return first.isPresent() ? first.get().getValue() : "";
    }

    private Flag getFlagByTeamId(int teamId) {
        Optional<Flag> first = flagMap.values().stream()
                .filter(e -> e.getBelongTo().equals(teamId))
                .findFirst();
        return first.orElse(null);
    }

    public String getTitle() {
        return runningCompetition.getTitle();
    }

    public Round getRound() {
        Round round = new Round();
        if (isRunning()){
            LocalDateTime startTime = runningCompetition.getStartTime();
            LocalDateTime nowTime = LocalDateTime.now();

            round.startTime = startTime.toEpochSecond(ZoneOffset.of("+8"));
            round.endTime = runningCompetition.getEndTime().toEpochSecond(ZoneOffset.of("+8"));
            round.nowTime = nowTime.toEpochSecond(ZoneOffset.of("+8"));

            long offset = LocalDateTimeUtil.between(startTime, nowTime).getSeconds();

            round.roundDuration = 60;
            round.nowRound = offset / round.roundDuration;
            round.roundRemainTime = round.roundDuration - (offset % round.roundDuration);
        }
        round.status = this.status;
        return round;

    }

    public void setSseEmitter(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
    }


    @Data
    @AllArgsConstructor
    public static class GameBox {
        private String title;
        private String teamName;
        private String entry;
        private int score;
        private boolean isAttacked;
        private String description;
    }

    @Data
    public static class Round {

        // timestamp
        private long startTime;
        private long endTime;
        private long nowTime;

        // 每轮持续时长 s
        private long roundDuration;
        private long nowRound;
        private long roundRemainTime;
        private CompetitionStatus status;
    }
}
