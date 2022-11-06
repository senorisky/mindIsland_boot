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
public class Elist implements Serializable {

    private static final long serialVersionUID = 1L;

    private String viewId;

    /**
     * list的数据 colum：，listobj
     */
    private String data;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


}
