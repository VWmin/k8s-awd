package com.vwmin.k8sawd.web.interceptor;

import cn.hutool.core.util.StrUtil;
import com.vwmin.k8sawd.web.exception.RoutineException;
import com.vwmin.k8sawd.web.model.ResponseCode;
import com.vwmin.k8sawd.web.service.ManagerService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/9 19:51
 */
@Component
public class ManagerAuthInterceptor implements HandlerInterceptor {

    private final ManagerService managerService;

    public ManagerAuthInterceptor(ManagerService managerService) {
        this.managerService = managerService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");
        if (StrUtil.isEmpty(token)){
            throw new RoutineException(ResponseCode.FAIL, "没有权限，请登录");
        }

        if (!managerService.existByToken(token)){
            throw new RoutineException(ResponseCode.FAIL, "token验证失败，请登录");
        }


        return true;
    }
}
