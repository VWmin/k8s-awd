package com.vwmin.k8sawd.web.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/13 20:17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("image")
public class Image extends BaseEntity{
    private String name;
    private String description;
}
