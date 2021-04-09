package com.vwmin.k8sawd.web.interceptor;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.vwmin.k8sawd.web.exception.RoutineException;
import com.vwmin.k8sawd.web.model.CompetitionHandler;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.model.ResponseCode;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.PrintWriter;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/7 15:32
 */
@Component
public class TeamRequestInterceptor implements HandlerInterceptor {

    private final CompetitionHandler competitionHandler;

    public TeamRequestInterceptor(CompetitionHandler competitionHandler) {
        this.competitionHandler = competitionHandler;
    }

    /**
     * 对team相关请求做检查，在创建competition前不允许team操作
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!competitionHandler.isSet()) {
            throw new RoutineException(ResponseCode.FAIL, "请先创建比赛.");
        }
//        else if (!competitionHandler.isRunning()) {
//            throw new RoutineException("比赛开始后无法修改队伍");
//        }
        return true;
    }
}
