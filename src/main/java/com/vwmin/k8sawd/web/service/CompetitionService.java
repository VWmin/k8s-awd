package com.vwmin.k8sawd.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vwmin.k8sawd.web.entity.Competition;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 11:41
 */
public interface CompetitionService extends IService<Competition> {


    /**
     * 通过传入的对象创建一个比赛，该比赛将会被设为running
     * @param competition 要创建的比赛模板
     */
    void createCompetition(Competition competition);
}
