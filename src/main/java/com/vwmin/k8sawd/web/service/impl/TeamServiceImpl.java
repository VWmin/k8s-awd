package com.vwmin.k8sawd.web.service.impl;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vwmin.k8sawd.web.entity.Team;
import com.vwmin.k8sawd.web.exception.RoutineException;
import com.vwmin.k8sawd.web.mapper.TeamMapper;
import com.vwmin.k8sawd.web.model.CompetitionHandler;
import com.vwmin.k8sawd.web.model.ResponseCode;
import com.vwmin.k8sawd.web.service.TeamService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 11:45
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {
    @Override
    public void addTeam(Team team, int competitionId) {
        team.setCompetitionId(competitionId);
        emptyCheck(team);
        repeatCheck(team);
        save(team);
    }

    @Override
    public void addTeams(List<Team> teams, int competitionId) {
        // 检查输入的队伍名之间是否有重复
        Set<String> inputNames = teams.stream().map(Team::getName).collect(Collectors.toSet());
        if (inputNames.size() != teams.size()) {
            throw new RoutineException("输入的队伍名存在重复");
        }

        for (Team team : teams) {
            team.setCompetitionId(competitionId);

            emptyCheck(team);

            repeatCheck(team);

            // 生成16位数字、字母格式密码
            team.setPassword(RandomUtil.randomString(16));

            // 生成32位hex格式token
            team.setSecretKey(HexUtil.encodeHexStr(RandomUtil.randomBytes(16)));
        }

        saveBatch(teams);
    }

    @Override
    public void editTeam(Team team, int competitionId) {
        team.setCompetitionId(competitionId);
        emptyCheck(team);
        updateById(team);
    }

    @Override
    public List<Team> teamsByCompetition(int competitionId) {
        LambdaUpdateWrapper<Team> condition = new LambdaUpdateWrapper<>();
        condition.eq(Team::getCompetitionId, competitionId);
        return list(condition);
    }

    @Override
    public void resetPassword(Team team, int competitionId) {
        team.setCompetitionId(competitionId);
        team.setPassword(RandomUtil.randomString(16));
        updateById(team);
    }

    private void repeatCheck(Team team) {
        LambdaQueryWrapper<Team> condition = new LambdaQueryWrapper<>();
        condition.eq(Team::getName, team.getName()).eq(Team::getCompetitionId, team.getCompetitionId());
        if (count(condition) != 0) {
            throw new RoutineException(ResponseCode.FAIL, "队伍名称[" + team.getName() + "]已存在");
        }
    }

    private void emptyCheck(Team team) {
        if (StrUtil.isEmpty(team.getName())) {
            throw new RoutineException(ResponseCode.FAIL, "队伍名称不能为空");
        } else if (StrUtil.isEmpty(team.getLogo())) {
            team.setLogo("default.png");
        }
    }
}
