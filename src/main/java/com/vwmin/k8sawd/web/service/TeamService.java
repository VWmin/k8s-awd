package com.vwmin.k8sawd.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vwmin.k8sawd.web.entity.Team;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 11:42
 */
public interface TeamService extends IService<Team> {

    /**
     * 添加一个队伍
     * @param team 要添加的队伍
     */
    void addTeam(Team team);

    void editTeam(Team team);

}
