package com.lifemind.bluer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

/**
 * @Classname Result
 * @Description TODO
 * @Date 2022/10/26 22:26
 * @Created by senorisky
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private HashMap<String, Object> data;
    private Integer code;
    private String msg;

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
