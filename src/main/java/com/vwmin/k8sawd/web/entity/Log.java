package com.vwmin.k8sawd.web.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 日志表
 * </p>
 *
 * @author vwmin
 * @since 2021-03-15
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("log")
public class Log extends BaseEntity {

    private static final long serialVersionUID = 1L;



    /**
     * 日志级别
     */
    private Integer level;

    /**
     * 类别
     */
    private String kind;

    /**
     * 内容
     */
    private String content;




}
