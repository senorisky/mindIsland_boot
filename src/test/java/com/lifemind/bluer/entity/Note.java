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
public class Note implements Serializable {

    private static final long serialVersionUID = 1L;

    private String noteId;

    private LocalDateTime createTime;

    private String noteInfo;

    private String noteIcon;

    private String noteName;

    private String userId;

    /**
     * 路由路径
     */
    private String path;

    private String type;

    private String fatherName;

    private String component;


}
