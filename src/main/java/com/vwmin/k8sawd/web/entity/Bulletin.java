package com.vwmin.k8sawd.web.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/13 18:28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bulletin")
public class Bulletin extends BaseEntity{
    private String title;
    private String content;
}
