package com.lifemind.bluer.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
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

    private String userId;

    private String email;

    private String password;

    private String userName;

    private LocalDateTime createTime;

    private LocalDateTime loginTime;


}
