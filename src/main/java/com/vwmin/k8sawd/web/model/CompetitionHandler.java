package com.vwmin.k8sawd.web.model;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.vwmin.k8sawd.web.entity.Competition;
import com.vwmin.k8sawd.web.entity.Flag;
import com.vwmin.k8sawd.web.entity.Team;
import com.vwmin.k8sawd.web.service.FlagService;
import com.vwmin.k8sawd.web.service.KubernetesService;
import com.vwmin.k8sawd.web.service.SystemService;
import com.vwmin.k8sawd.web.service.TeamService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final FlagService flagService;
    private final TeamService teamService;
    private final SystemService systemService;
    private final KubernetesService kubernetesService;
    private final Scheduler scheduler;


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

    public int getId(){
        return this.runningCompetition.getId();
    }

    public void setRunningCompetition(Competition runningCompetition) {
        this.runningCompetition = runningCompetition;
    }

    /**
     * 检查比赛是否创建且正在进行
     * @return 检查结果
     */
    public boolean isRunning(){
        return this.runningCompetition != null && runningCompetition.getStartTime().isBefore(LocalDateTimeUtil.now());
    }

    /**
     * 检查比赛是否创建
     * @return 检查结果
     */
    public boolean isSet(){
        return this.runningCompetition != null;
    }

    /**
     * 统计上轮得分
     * 将所有队伍的flag设为expired，并写入数据库
     */
    public void roundCheck() {
        if (!flagMap.isEmpty()){
            statistic();
            flushFlag();
        }
    }

    private void statistic(){
        Map<Integer, List<Flag>> collect = flagMap.values().stream().filter(Flag::isUsed)
                .collect(Collectors.groupingBy(Flag::getUsedBy));
        int baseScore = runningCompetition.getScore();
        collect.forEach((k, v) -> {
            int plusScore = baseScore*v.size();

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
     * @return 本次提交是否成功
     */
    public boolean validFlag(int teamId, String flagVal) {
        // 检查是不是一个flag
        if (!flagMap.containsKey(flagVal)) {
            return false;
        }


        Flag flag = flagMap.get(flagVal);
        // 检查是否已使用或是自己队伍的flag
        if (flag.isUsed() || flag.getBelongTo().equals(teamId)) {
            return false;
        } else {
            flag.setUsed(true);
            flag.setUsedBy(teamId);
        }


        return true;
    }

    public void finishAll() throws SchedulerException {
        // fixme 结束所有定时任务
        scheduler.clear();
        kubernetesService.clearResource();
        systemService.finishAll();
        this.runningCompetition = null;
    }

    public String getFlagByTeamId(int teamId) {
        Optional<Flag> first = flagMap.values().stream()
                .filter(e -> e.getBelongTo().equals(teamId))
                .findFirst();
        return first.isPresent() ? first.get().getValue() : "";
    }
}
