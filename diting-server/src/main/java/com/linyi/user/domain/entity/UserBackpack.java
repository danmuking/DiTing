package com.linyi.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户背包表
 * </p>
 *
 * @author lin
 * @since 2024-01-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_backpack")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBackpack implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * uid
     */
    @TableField("uid")
    private Long uid;

    /**
     * 物品id
     */
    @TableField("item_id")
    private Long itemId;

    /**
     * 使用状态 0.待使用 1已使用
     */
    @TableField("status")
    private Integer status;

    /**
     * 幂等号
     */
    @TableField("idempotent")
    private String idempotent;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;


}
