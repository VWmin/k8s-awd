package com.vwmin.k8sawd.web.controller;

import com.vwmin.k8sawd.web.entity.Manager;
import com.vwmin.k8sawd.web.exception.RoutineException;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.model.ResponseCode;
import com.vwmin.k8sawd.web.service.ManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/17 18:23
 */
@RestController
@RequestMapping("/manager")
public class LoginController {


    private final ManagerService managerService;

    public LoginController(ManagerService managerService) {
        this.managerService = managerService;
    }

    /**
     * 登录验证
     * @param manager 需要参数 name 、 password
     * @return token
     */
    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestBody Manager manager){

        if (managerService.login(manager)){
            // 登录成功 向数据库刷新token
            return Response.success(managerService.refreshToken(manager));
        } else {
            // 登录失败
            throw new RoutineException(ResponseCode.FAIL, "管理员登录失败");
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<Response> logout(HttpServletRequest request){

        // 清空该用户token
        managerService.clearToken(request.getHeader("Authorization"));

        return Response.success("登出成功");
    }
}
