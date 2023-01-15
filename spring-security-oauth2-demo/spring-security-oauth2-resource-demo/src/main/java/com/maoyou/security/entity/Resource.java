package com.maoyou.security.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 资源表
 * </p>
 *
 * @author maoyou
 * @since 2022-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Resource implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 功能资源ID
     */
    @TableId("RESOURCEID")
    private String resourceid;

    /**
     * 父级功能资源ID
     */
    @TableField("PRESOURCEID")
    private String presourceid;

    /**
     * 功能名称
     */
    @TableField("NAME")
    private String name;

    /**
     * 功能路径
     */
    @TableField("URL")
    private String url;

    /**
     * 图标名称
     */
    @TableField("ICON")
    private String icon;

    /**
     * 安全策略【01：无需登录即可访问；02：登录即可访问；03：授权即可访问】
     */
    @TableField("SECURITYPOLICY")
    private String securitypolicy;

    /**
     * 有效性
     */
    @TableField("EFFECTIVE")
    private String effective;

    /**
     * 创建人
     */
    @TableField("CREATEUSER")
    private String createuser;

    /**
     * 创建时间
     */
    @TableField("CREATETIME")
    private LocalDateTime createtime;

    /**
     * 修改人
     */
    @TableField("MODIFYUSER")
    private String modifyuser;

    /**
     * 修改时间
     */
    @TableField("MODIFYTIME")
    private LocalDateTime modifytime;

    /**
     * 排序号
     */
    @TableField("ORDERNO")
    private Integer orderno;

    /**
     * 删除标识
     */
    @TableField("DELETED")
    private String deleted;


}
