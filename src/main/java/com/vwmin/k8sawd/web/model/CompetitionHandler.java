package com.vwmin.k8sawd.web.model;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.vwmin.k8sawd.web.entity.Competition;
import com.vwmin.k8sawd.web.entity.Flag;
import com.vwmin.k8sawd.web.entity.Team;
import com.vwmin.k8sawd.web.enums.CompetitionStatus;
import com.vwmin.k8sawd.web.enums.LogKind;
import com.vwmin.k8sawd.web.enums.LogLevel;
import com.vwmin.k8sawd.web.exception.RoutineException;
import com.vwmin.k8sawd.web.service.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    private final ConcurrentHashMap<Integer, String> teamNameMap;
    private final FlagService flagService;
    private final TeamService teamService;
    private final SystemService systemService;
    private final LogService logService;
    private final KubernetesService kubernetesService;
    private final Scheduler scheduler;
    private SseEmitter sseEmitter = null;

    private CompetitionStatus status = CompetitionStatus.UNSET;

    public final int defaultRoundDuration = 120; // second


    public CompetitionHandler(FlagService flagService, TeamService teamService,
                              SystemService systemService, LogService logService,
                              KubernetesService kubernetesService, Scheduler scheduler) {
        this.flagService = flagService;
        this.teamService = teamService;
        this.systemService = systemService;
        this.logService = logService;
        this.kubernetesService = kubernetesService;
        this.scheduler = scheduler;
        runningCompetition = null;
        flagMap = new ConcurrentHashMap<>();
        teamNameMap = new ConcurrentHashMap<>();
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
        logService.log(LogLevel.IMPORTANT, LogKind.SYSTEM,
                "比赛[%s]已设置，将于[%s]开始，预计结束于[%s]",
                runningCompetition.getTitle(), runningCompetition.getStartTime(), runningCompetition.getEndTime());
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

        // 设置队伍缓存
        if (teamNameMap.isEmpty()) {
            List<Team> teams = teamService.teamsByCompetition(getId());
            for (Team team : teams) {
                teamNameMap.put(team.getId(), team.getName());
            }
        }

        // 存在flag记录则统计
        if (!flagMap.isEmpty()) {
            statistic();
            flushFlag();
        }
    }

    private void statistic() {
        // 攻击得分
        Map<Integer, List<Flag>> collect = flagMap.values().stream().filter(Flag::isUsed)
                .collect(Collectors.groupingBy(Flag::getUsedBy));
        Map<Integer, Integer> magnification = new HashMap<>();
        collect.forEach((k, v) -> magnification.put(k, v.size()));

        // 防御得分
        flagMap.values().stream().filter(e -> !e.isUsed()).forEach(e -> {
            Integer key = e.getBelongTo();
            Integer after = magnification.getOrDefault(key, 0) + 1;
            magnification.put(key, after);
        });

        int baseScore = runningCompetition.getScore();
        long round = nowRound();
        magnification.forEach((k, v) -> {
            int plusScore = baseScore * v;

            // 向数据库写入得分
            Team team = teamService.getById(k);
            team.plusScore(plusScore);
            teamService.updateById(team);

            logService.log(LogLevel.NORMAL, LogKind.SYSTEM,
                    "Round[%d]，队伍[%s]，得分：%d", round, team.getName(), plusScore);
        });
        logService.log(LogLevel.WARNING, LogKind.SYSTEM, "第%d轮统计完成.", round);
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
        logService.log(LogLevel.NORMAL, LogKind.SYSTEM,
                "Round[%d]，队伍[%s]，生成Flag[%s]", nowRound(), teamNameMap.get(teamId), flagVal);
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

            String attacker = teamNameMap.get(teamId);
            String victim = teamNameMap.get(flag.getBelongTo());

            logService.log(LogLevel.NORMAL, LogKind.SYSTEM,
                    "Round[%d]，队伍[%s]成功提交了队伍[%s]的Flag", nowRound(), attacker, victim);

            try {
                if (sseEmitter != null) {
                    sseEmitter.send(LiveLogEvent.AttackEvent(attacker, victim, getTitle()));
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
//        teamService.removeAll();
        scheduler.clear();
        kubernetesService.clearResource();
        systemService.finishAll();
        this.sseEmitter = null;
        this.teamNameMap.clear();
        this.status = CompetitionStatus.FINISHED;
        logService.log(LogLevel.IMPORTANT, LogKind.SYSTEM, "比赛结束!");
    }

    @PreDestroy
    public void exit() throws SchedulerException {
        log.info("exiting...");
        finishAll();
    }

    public GameBox gameBoxByTeamId(int teamId) {
        Flag flag = getFlagByTeamId(teamId);

        String title = "web服务入口";
        String entry = kubernetesService.serviceEntry(getId(), teamId);
        return new GameBox(title, teamNameMap.get(teamId), entry, flag != null && flag.isUsed(),
                "靶机web服务入口");
    }

    public GameBox sshEntryByTeamId(int teamId) {
        String title = "ssh服务入口";
        String entry = kubernetesService.sshEntry(getId(), teamId);
        return new GameBox(title, teamNameMap.get(teamId), entry, false,
                "靶机ssh服务入口，请自行使用ssh客户端连接");
    }

    public boolean isAttacked(int teamId) {
        Flag flagByTeamId = getFlagByTeamId(teamId);
        return isRunning() && (flagByTeamId != null && flagByTeamId.isUsed());
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

    private long nowRound() {
        LocalDateTime startTime = runningCompetition.getStartTime();
        LocalDateTime nowTime = LocalDateTime.now();
        return LocalDateTimeUtil.between(startTime, nowTime).getSeconds() / defaultRoundDuration + 1;
    }

    public Round getRound() {
        Round round = new Round();
        if (isRunning()) {
            LocalDateTime startTime = runningCompetition.getStartTime();
            LocalDateTime nowTime = LocalDateTime.now();

            round.startTime = startTime.toEpochSecond(ZoneOffset.of("+8"));
            round.endTime = runningCompetition.getEndTime().toEpochSecond(ZoneOffset.of("+8"));
            round.nowTime = nowTime.toEpochSecond(ZoneOffset.of("+8"));

            long offset = LocalDateTimeUtil.between(startTime, nowTime).getSeconds();

            round.roundDuration = defaultRoundDuration;
            round.nowRound = offset / round.roundDuration + 1;
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
