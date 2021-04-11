package com.vwmin.k8sawd.web.aop;

import com.vwmin.k8sawd.web.enums.CompetitionStatus;
import com.vwmin.k8sawd.web.exception.RoutineException;
import com.vwmin.k8sawd.web.model.CompetitionHandler;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.model.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/11 18:24
 */
@Slf4j
@Aspect
@Component
public class CheckCompetitionStatus {

    private final CompetitionHandler handler;

    public CheckCompetitionStatus(CompetitionHandler handler) {
        this.handler = handler;
    }

    @Pointcut("@annotation(com.vwmin.k8sawd.web.aop.ExpectedStatus)")
    public void access() {
    }

    @Before("access()")
    public void doBefore() {
//        log.info("环切测试");
    }

    @Around("access()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        ExpectedStatus annotation = signature.getMethod().getAnnotation(ExpectedStatus.class);
        CompetitionStatus[] expected = annotation.expected();

        for (CompetitionStatus status : expected){
            if (status == handler.status()){
                return pjp.proceed();
            }
        }

        return Response.error(ResponseCode.FAIL, "当前比赛状态[ " + handler.status() + " ]不支持该操作");
    }

}
