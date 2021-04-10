package com.vwmin.k8sawd.web.service.impl;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vwmin.k8sawd.web.entity.Competition;
import com.vwmin.k8sawd.web.entity.System;
import com.vwmin.k8sawd.web.mapper.SystemMapper;
import com.vwmin.k8sawd.web.model.CompetitionHandler;
import com.vwmin.k8sawd.web.service.SystemService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 11:44
 */
@Service
public class SystemServiceImpl extends ServiceImpl<SystemMapper, System> implements SystemService {


    /**
     * 初始化一些必须要被初始化的配置项，如果不存在
     */
    @PostConstruct
    private void init() {
        for (String item : System.NECESSARY_CONF) {
            if (!exist(item)) {
                put(item, System.VAL_NULL);
            }
        }
    }


    @Override
    public int runningCompetition() {
        LambdaQueryWrapper<System> condition = new LambdaQueryWrapper<>();
        condition.eq(System::getSysKey, System.KEY_RUNNING_COMPETITION);
        String val = getOne(condition).getSysValue();

        return NumberUtil.isInteger(val)
                ? Integer.parseInt(val)
                : -1;
    }

    @Override
    public void setCompetition(Competition competition) {
        // 将正在进行的比赛设置为传入的参数
        put(System.KEY_RUNNING_COMPETITION, competition.getId().toString());
    }

    @Override
    public void finishAll() {
        put(System.KEY_RUNNING_COMPETITION, System.VAL_NULL);
    }

    /**
     * fixme: 不检查是否存在key，所以给定的key必须存在
     */
    @Override
    public void put(String key, String value) {
        LambdaUpdateWrapper<System> condition = new LambdaUpdateWrapper<>();
        condition.eq(System::getSysKey, key);
        saveOrUpdate(new System(key, value), condition);
    }

    private boolean exist(String key) {
        LambdaUpdateWrapper<System> condition = new LambdaUpdateWrapper<>();
        condition.eq(System::getSysKey, key);
        return count(condition) > 0;
    }


}
