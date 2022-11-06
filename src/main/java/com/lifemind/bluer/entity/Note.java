package com.lifemind.bluer.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
public class Note implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.INPUT)
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
    @TableField(exist = false)
    private Boolean IsOpen = false;

    @TableField(exist = false)
    private List<View> children ;

}
