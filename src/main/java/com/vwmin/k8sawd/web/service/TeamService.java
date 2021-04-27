package com.vwmin.k8sawd.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vwmin.k8sawd.web.entity.Team;

import java.util.List;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 11:42
 */
public interface TeamService extends IService<Team> {

    /**
     * 添加一个队伍
     *
     * @param team 要添加的队伍
     * @param competitionId 所属比赛id
     */
    void addTeam(Team team, int competitionId);

    /**
     * 添加一组队伍
     * @param teams 要添加的队伍
     * @param competitionId 所属比赛id
     */
    void addTeams(List<Team> teams, int competitionId);

    /**
     * 修改一个队伍
     * @param team 要修改的队伍
     */
    void editTeam(Team team);

    /**
     * 通过比赛id查询队伍
     * @param competitionId 比赛id
     * @return 查询结果
     */
    List<Team> teamsByCompetition(int competitionId);

    /**
     * 重置队伍密码
     * @param team 需要重置的队伍
     */
    void resetPassword(Team team);

    /**
     * 队伍登录
     * @param team 登录表单
     * @return 登录结果
     */
    boolean login(Team team);

    /**
     * 获得一个队伍的token
     * @param team 要查询的队伍
     * @return 查询结果
     */
    String getToken(Team team);

    /**
     * 通过队伍token查询队伍
     * @param token token
     * @return 查詢结果
     */
    Team getTeamByToken(String token);

    /**
     * 清除一场比赛关联的队伍
     */
    void removeAll();
}
