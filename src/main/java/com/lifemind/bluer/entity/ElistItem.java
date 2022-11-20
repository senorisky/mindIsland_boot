package com.lifemind.bluer.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Classname ElistItem
 * @Description TODO
 * @Date 2022/11/9 21:35
 * @Created by senorisky
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElistItem {
    private String colum;
    private List<JSONObject> items;
}
