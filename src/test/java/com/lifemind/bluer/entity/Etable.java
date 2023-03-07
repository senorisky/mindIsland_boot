package com.lifemind.bluer.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class Etable implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * table的数据 [{colum1:data1,colum2:data2},......]
     */
    private String data;

    private String colum;

    private String viewId;


}
