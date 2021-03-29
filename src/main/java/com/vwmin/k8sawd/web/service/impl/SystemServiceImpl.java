package com.vwmin.k8sawd.web.service.impl;

import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vwmin.k8sawd.web.entity.Competition;
import com.vwmin.k8sawd.web.entity.System;
import com.vwmin.k8sawd.web.mapper.SystemMapper;
import com.vwmin.k8sawd.web.service.CompetitionService;
import com.vwmin.k8sawd.web.service.SystemService;
import org.springframework.stereotype.Service;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 11:44
 */
@Service
public class SystemServiceImpl extends ServiceImpl<SystemMapper, System> implements SystemService {

    private final CompetitionService competitionService;

    public SystemServiceImpl(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    @Override
    public Pair<Boolean, Competition> runningCompetition() {
        // 如果不存在alive的比赛，返回false
        if (!hasAlive()){
            return new Pair<>(false, null);
        }

        // 找到alive的比赛并返回
        return new Pair<>(true, getAlive());
    }

    @Override
    public void setCompetition(Competition competition) {
        // 如果已经存在alive的比赛，则什么都不做
        if (hasAlive()){
            return;
        }

        // 否则记录传入的比赛，并设置为alive
        System record = new System();
        record.setCompetitionId(competition.getId());
        record.setIsAlive(true);

        save(record);
    }

    private boolean hasAlive(){
        LambdaQueryWrapper<System> condition = new LambdaQueryWrapper<>();
        condition.eq(System::getIsAlive, true);
        return count(condition) == 1;
    }

    private Competition getAlive(){
        LambdaQueryWrapper<System> condition = new LambdaQueryWrapper<>();
        condition.eq(System::getIsAlive, true);
        return competitionService.getById(getOne(condition).getCompetitionId());
    }
}
