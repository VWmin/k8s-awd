package com.vwmin.k8sawd.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vwmin.k8sawd.web.entity.Competition;
import com.vwmin.k8sawd.web.exception.RoutineException;
import com.vwmin.k8sawd.web.mapper.CompetitionMapper;
import com.vwmin.k8sawd.web.model.ResponseCode;
import com.vwmin.k8sawd.web.service.CompetitionService;
import com.vwmin.k8sawd.web.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 11:45
 */
@Service
public class CompetitionServiceImpl extends ServiceImpl<CompetitionMapper, Competition> implements CompetitionService {

    private final SystemService systemService;;

    public CompetitionServiceImpl(SystemService systemService) {
        this.systemService = systemService;
    }

    @Override
    public void createCompetition(Competition competition) {
        // 如果有正在进行的比赛，则创建失败
        if (systemService.hasAlive()){
            throw new RoutineException(ResponseCode.FAIL, "已有正在进行的比赛");
        }

        // 检查起止时间适合符合语义
        checkTime(competition.getStartTime(), competition.getEndTime());

        // 写入记录，并设置为alive
        save(competition);
        systemService.setCompetition(competition);
    }

    private void checkTime(LocalDateTime startTime, LocalDateTime endTime){
        // todo
    }



}
