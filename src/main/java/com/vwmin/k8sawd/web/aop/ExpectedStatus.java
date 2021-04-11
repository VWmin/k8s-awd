package com.vwmin.k8sawd.web.aop;

import com.vwmin.k8sawd.web.enums.CompetitionStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/11 18:22
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpectedStatus {
    CompetitionStatus[] expected();
}
