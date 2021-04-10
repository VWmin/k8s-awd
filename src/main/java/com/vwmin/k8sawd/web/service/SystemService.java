package com.vwmin.k8sawd.web.service;

import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.extension.service.IService;
import com.vwmin.k8sawd.web.entity.Competition;
import com.vwmin.k8sawd.web.entity.System;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 11:43
 */
public interface SystemService extends IService<System> {

    /**
     * 获得一个当前运行中的比赛
     * @return 是否存在，不存在返回-1
     */
     int runningCompetition();


    /**
     * 设置该比赛为running
     * @param competition 要记录的比赛
     */
    void setCompetition(Competition competition);


    /**
     * 将所有比赛设置为false，开发用
     */
    void finishAll();


    /**
     * 创建或更新一条配置
     * @param key key
     * @param val val
     */
    void put(String key, String val);


}
