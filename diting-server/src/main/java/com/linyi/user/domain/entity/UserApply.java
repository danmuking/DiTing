package com.linyi.user.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 用户申请表
 * </p>
 *
 * @author lin
 * @since 2024-01-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("user_apply")
public class UserApply implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 申请人uid
     */
    @TableField("uid")
    private Long uid;

    /**
     * 申请类型 1加好友
     */
    @TableField("type")
    private Integer type;

    /**
     * 接收人uid
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 申请信息
     */
    @TableField("msg")
    private String msg;

    /**
     * 申请状态 1待审批 2同意
     */
    @TableField("status")
    private Integer status;

    /**
     * 阅读状态 1未读 2已读
     */
    @TableField("read_status")
    private Integer readStatus;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

}
