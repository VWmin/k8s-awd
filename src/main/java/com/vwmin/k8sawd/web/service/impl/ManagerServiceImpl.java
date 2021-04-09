package com.vwmin.k8sawd.web.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vwmin.k8sawd.web.entity.BaseEntity;
import com.vwmin.k8sawd.web.entity.Manager;
import com.vwmin.k8sawd.web.exception.RoutineException;
import com.vwmin.k8sawd.web.mapper.ManagerMapper;
import com.vwmin.k8sawd.web.service.ManagerService;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/9 13:14
 */
@Service
public class ManagerServiceImpl extends ServiceImpl<ManagerMapper, Manager> implements ManagerService {
    @Override
    public void addManager(Manager manager) {
        // 账号 密码 不能为空
        if (StrUtil.isEmpty(manager.getName()) || StrUtil.isEmpty(manager.getPassword())) {
            throw new RoutineException("名称或密码不能为空");
        }

        // 账号 不能重复
        if (existByName(manager.getName())) {
            throw new RoutineException("该名称已被使用");
        }

        // todo 密码hash

        save(manager);
    }

    @Override
    public void updatePassword(Manager manager) {
        String newPass = manager.getPassword();
        Integer id = manager.getId();
        LambdaUpdateWrapper<Manager> condition = new LambdaUpdateWrapper<>();
        condition.eq(BaseEntity::getId, id).set(Manager::getPassword, newPass);
        update(condition);
    }

    @Override
    public String refreshToken(Manager manager) {
        String token = UUID.randomUUID().toString();

        LambdaUpdateWrapper<Manager> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Manager::getId, manager.getId())
                .or().eq(Manager::getName, manager.getName())
                .set(Manager::getToken, token);

        update(updateWrapper);

        return token;
    }

    @Override
    public boolean login(Manager manager) {
        // 在数据库中查询是否存在用户名、密码匹配的管理账户
        LambdaQueryWrapper<Manager> condition = new LambdaQueryWrapper<>();
        condition.eq(Manager::getName, manager.getName())
                .eq(Manager::getPassword, manager.getPassword())
                .last("limit 1");
        return count(condition) == 1;
    }

    @Override
    public void clearToken(String token) {
        if (StrUtil.isNotEmpty(token)){
            LambdaUpdateWrapper<Manager> condition = new LambdaUpdateWrapper<>();
            condition.eq(Manager::getToken, token).set(Manager::getToken, "");
            update(condition);
        }
    }

    @Override
    public boolean existByName(String managerName) {
        LambdaQueryWrapper<Manager> condition = new LambdaQueryWrapper<>();
        condition.eq(Manager::getName, managerName);
        return count(condition) != 0;
    }

    @Override
    public boolean existByToken(String token) {
        LambdaQueryWrapper<Manager> condition = new LambdaQueryWrapper<>();
        condition.eq(Manager::getToken, token);
        return count(condition) != 0;
    }
}
