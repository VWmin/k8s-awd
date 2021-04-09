package com.vwmin.k8sawd.web.enums;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/28 17:05
 */
public enum LogKind {
    MANAGER_OPERATE("manager_operate");

    private final String value;

    LogKind(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
