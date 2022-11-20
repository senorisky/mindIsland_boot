package com.lifemind.bluer.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
public class Etable implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String viewId;


    /**
     * table的数据
     */
    private String data;

    @TableField(exist = false)
    private List<JSONObject> datas;//每一行键值对的数据[{key1:v1,k2:v2.....},{}...]

    @TableField(exist = false)
    private List<String> colums;

    private String colum;

}
