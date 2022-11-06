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
public class Etable implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String viewId;


    /**
     * table的数据 [{colum1:data1,colum2:data2},......]
     */
    private String data;

    private String colums;

}
