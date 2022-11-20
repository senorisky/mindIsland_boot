package com.lifemind.bluer.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 *
 * </p>
 *
 * @author ckz
 * @since 2022-11-06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Gallery implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(exist = false)
    private List<JSONObject> datas;
    private String viewId;

    /**
     * list string  图片的路径
     */
    private String data;
}
