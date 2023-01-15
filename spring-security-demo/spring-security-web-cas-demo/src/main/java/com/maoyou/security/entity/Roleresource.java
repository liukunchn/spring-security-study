package com.maoyou.security.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 角色资源表
 * </p>
 *
 * @author maoyou
 * @since 2022-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Roleresource implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色id
     */
    @TableId("ROLEID")
    private String roleid;

    /**
     * 资源id
     */
    @TableField("RESOURCEID")
    private String resourceid;

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
