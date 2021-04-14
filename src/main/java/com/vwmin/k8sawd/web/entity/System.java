package com.vwmin.k8sawd.web.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 11:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys")
public class System extends BaseEntity{

    public static final String KEY_RUNNING_COMPETITION = "runningCompetition";
    public static final String KEY_IMAGE = "image";
    public static final String VAL_NULL = "null";

    // 必须被初始化的配置项
    public static final String[] NECESSARY_CONF = {KEY_RUNNING_COMPETITION, KEY_IMAGE};

    private String sysKey;

    private String sysValue;

    public System(String sysKey, String sysValue) {
        this.sysKey = sysKey;
        this.sysValue = sysValue;
    }

    public System(){}
}
