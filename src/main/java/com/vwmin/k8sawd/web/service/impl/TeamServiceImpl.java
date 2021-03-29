package com.vwmin.k8sawd.web.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vwmin.k8sawd.web.entity.Team;
import com.vwmin.k8sawd.web.exception.RoutineException;
import com.vwmin.k8sawd.web.mapper.TeamMapper;
import com.vwmin.k8sawd.web.model.ResponseCode;
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
        emptyCheck(team);
        save(team);
    }

    @Override
    public void editTeam(Team team) {
        emptyCheck(team);
        updateById(team);
    }

    private void emptyCheck(Team team) {
        if (StrUtil.isEmpty(team.getName())) {
            throw new RoutineException(ResponseCode.FAIL, "队伍名称不能为空");
        } else if (StrUtil.isEmpty(team.getLogo())) {
            team.setLogo("default.png");
        }
    }
}
