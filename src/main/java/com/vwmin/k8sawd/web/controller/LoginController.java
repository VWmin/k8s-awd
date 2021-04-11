package com.vwmin.k8sawd.web.controller;

import com.vwmin.k8sawd.web.aop.ExpectedStatus;
import com.vwmin.k8sawd.web.entity.Manager;
import com.vwmin.k8sawd.web.entity.Team;
import com.vwmin.k8sawd.web.enums.CompetitionStatus;
import com.vwmin.k8sawd.web.exception.RoutineException;
import com.vwmin.k8sawd.web.model.CompetitionHandler;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.model.ResponseCode;
import com.vwmin.k8sawd.web.service.ManagerService;
import com.vwmin.k8sawd.web.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/17 18:23
 */
@RestController
public class LoginController {


    private final ManagerService managerService;
    private final TeamService teamService;
    private final CompetitionHandler competitionHandler;

    public LoginController(ManagerService managerService, TeamService teamService, CompetitionHandler competitionHandler) {
        this.managerService = managerService;
        this.teamService = teamService;
        this.competitionHandler = competitionHandler;
    }


    @PostMapping("/manager/login")
    public ResponseEntity<Response> login(@RequestBody Manager manager) {

        if (managerService.login(manager)) {
            // 登录成功 向数据库刷新token
            return Response.success(managerService.refreshToken(manager));
        } else {
            // 登录失败
            throw new RoutineException(ResponseCode.FAIL, "管理员登录失败");
        }
    }

    @GetMapping("/manager/logout")
    public ResponseEntity<Response> logout(@RequestHeader("Authorization") String token) {

        // 清空该用户token
        managerService.clearToken(token);

        return Response.success("登出成功");
    }

    @RequestMapping("/login")
    @ExpectedStatus(expected = {CompetitionStatus.SET, CompetitionStatus.RUNNING, CompetitionStatus.FINISHED})
    public ResponseEntity<Response> login(@RequestBody Team team) {

        if (teamService.login(team)) {
            return Response.success(teamService.getToken(team));
        } else {
            throw new RoutineException(ResponseCode.FAIL, "登录验证失败");
        }

    }

    @RequestMapping("/logout")
    public ResponseEntity<Response> playerLogout() {
        return Response.success("登出成功");
    }
}
