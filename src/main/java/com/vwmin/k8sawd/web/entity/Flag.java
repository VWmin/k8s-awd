package com.vwmin.k8sawd.web.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 11:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("flag")
public class Flag extends BaseEntity{

    private String value;

    private boolean isUsed;

    private Integer belongTo;

    private Integer usedBy;

    public Flag(Integer belongTo, String value){
        this.value = value;
        this.belongTo = belongTo;
        this.isUsed = false;
    }

    public Flag(){}

}
