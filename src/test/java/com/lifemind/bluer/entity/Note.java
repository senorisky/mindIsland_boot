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
 * @since 2023-03-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Note implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private LocalDateTime createTime;

    private String info;

    private String icon;

    private String name;

    private String userId;

    /**
     * 路由路径
     */
    private String path;

    private String type;

    private String fname;

    private String component;


}
