package com.maoyou.security.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户角色关系表
 * </p>
 *
 * @author maoyou
 * @since 2022-07-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Userrole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 人员id
     */
    @TableId("USERID")
    private String userid;

    /**
     * 角色id
     */
    @TableField("ROLEID")
    private String roleid;

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
