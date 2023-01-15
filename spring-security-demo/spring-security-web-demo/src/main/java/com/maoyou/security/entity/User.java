package com.maoyou.security.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author maoyou
 * @since 2022-07-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户编号
     */
    @TableId("USERID")
    private String userid;

    /**
     * 登录账号
     */
    @TableField("USERNAME")
    private String username;

    /**
     * 登录密码
     */
    @TableField("PASSWORD")
    private String password;

    /**
     * 密码错误次数
     */
    @TableField("PASSWORDDEFAULTNUM")
    private Integer passworddefaultnum;

    /**
     * 密码最后修改时间
     */
    @TableField("PWDLASTMODIFYDATE")
    private Date pwdlastmodifydate;

    /**
     * 是否锁定
     */
    @TableField("LOCKED")
    private String locked;

    /**
     * 姓名
     */
    @TableField("NAME")
    private String name;

    /**
     * 性别
     */
    @TableField("SEX")
    private String sex;

    /**
     * 证件号码
     */
    @TableField("IDCARDNO")
    private String idcardno;

    /**
     * 手机号码
     */
    @TableField("MOBILE")
    private String mobile;

    /**
     * 创建人
     */
    @TableField("CREATEUSER")
    private String createuser;

    /**
     * 创建时间
     */
    @TableField("CREATETIME")
    private Date createtime;

    /**
     * 修改时间
     */
    @TableField("MODIFYTIME")
    private Date modifytime;

    /**
     * 销毁标识
     */
    @TableField("DESTORY")
    private String destory;

    /**
     * 排序号
     */
    @TableField("ORDERNO")
    private Integer orderno;

    /**
     * 有效标识
     */
    @TableField("DELETED")
    private String deleted;


}
