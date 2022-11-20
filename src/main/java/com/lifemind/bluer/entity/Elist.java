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
public class Elist implements Serializable {

    private static final long serialVersionUID = 1L;

    private String viewId;

    /**
     * list的数据 [] colum:"",items[]
     */
    private String data;

    @TableField(exist = false)
    private List<ElistItem> datas;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;


}
