package com.vwmin.k8sawd.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vwmin.k8sawd.web.entity.Team;
import com.vwmin.k8sawd.web.mapper.TeamMapper;
import com.vwmin.k8sawd.web.service.TeamService;
import org.springframework.stereotype.Service;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 11:45
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {
    @Override
    public void addTeam(Team team) {

    }
}
