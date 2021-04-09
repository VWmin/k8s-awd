package com.vwmin.k8sawd.web.enums;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/19 21:41
 */
public enum LogLevel {
    NORMAL(0),
    WARNING(1),
    IMPORTANT(2);

    private final int val;

    LogLevel(int val){
        this.val = val;
    }

    public int val() {
        return val;
    }
}
