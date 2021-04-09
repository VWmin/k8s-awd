package com.vwmin.k8sawd.web.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * <p>
 * 管理员表
 * </p>
 *
 * @author vwmin
 * @since 2021-03-15
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@TableName("manager")
public class Manager extends BaseEntity {

    private static final long serialVersionUID = 1L;


    /**
     * 管理员名称
     */
    private String name;

    /**
     * 管理员密码
     */
    private String password;

    /**
     * 确保单点登录，登录时获得
     */
    private String token;

}
