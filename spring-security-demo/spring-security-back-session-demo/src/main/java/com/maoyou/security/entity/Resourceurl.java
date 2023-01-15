package com.maoyou.security.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 资源URL表
 * </p>
 *
 * @author maoyou
 * @since 2022-07-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Resourceurl implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资源ID
     */
    @TableId("RESOURCEID")
    private String resourceid;

    /**
     * URLID
     */
    @TableField("URLID")
    private String urlid;

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
