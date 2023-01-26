package com.lifemind.bluer.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author ckz
 * @since 2022-11-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "user_id", type = IdType.INPUT)
    private String userId;

    private String email;

    private String password;

    private String userName;

    private String avatar;

    private String des;

    private LocalDateTime createTime;

    private LocalDateTime loginTime;

    private String lock;
    private String on;
}
