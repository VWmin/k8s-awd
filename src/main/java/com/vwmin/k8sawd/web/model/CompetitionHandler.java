package com.vwmin.k8sawd.web.model;

import cn.hutool.core.lang.Pair;
import com.vwmin.k8sawd.web.entity.Competition;
import com.vwmin.k8sawd.web.entity.Flag;
import com.vwmin.k8sawd.web.service.FlagService;
import com.vwmin.k8sawd.web.service.SystemService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/6 19:24
 */
@Component
public class CompetitionHandler {

    // 需要被CompetitionService初始化
    private Competition runningCompetition;
    private final ConcurrentHashMap<String, Flag> flagMap;
    private final FlagService flagService;


    public CompetitionHandler(FlagService flagService) {
        this.flagService = flagService;
        runningCompetition = null;
        flagMap = new ConcurrentHashMap<>();
    }

    public Competition getRunningCompetition() {
        return this.runningCompetition;
    }

    public void setRunningCompetition(Competition runningCompetition) {
        this.runningCompetition = runningCompetition;
    }

    public boolean isRunning(){
        return this.runningCompetition != null;
    }

    /**
     * 将所有队伍的flag设为expired，并写入数据库
     */
    public void flushFlag() {
        if (!flagMap.isEmpty()){
            Collection<Flag> values = flagMap.values();
            flagService.saveBatch(values);
            flagMap.clear();
        }
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

    public String getFlagByTeamId(int teamId) {
        Optional<Flag> first = flagMap.values().stream()
                .filter(e -> e.getBelongTo().equals(teamId))
                .findFirst();
        return first.isPresent() ? first.get().getValue() : "";
    }
}
