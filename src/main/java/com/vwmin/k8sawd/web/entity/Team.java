package com.vwmin.k8sawd.web.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 11:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("team")
public class Team extends BaseEntity{

    private String name;

    private String logo;
}
