package com.vwmin.k8sawd.web.enums;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/10 18:13
 */
public enum CompetitionStatus {
    /**
     * 程序启动，无比赛设置
     */
    UNSET,
    /**
     * 有比赛被设置，但还未开始
     */
    SET,
    /**
     * 比赛正在进行
     */
    RUNNING,
    /**
     * 上一个比赛结束，且没有新的比赛被设置
     */
    FINISHED
}
