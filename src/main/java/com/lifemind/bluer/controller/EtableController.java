package com.lifemind.bluer.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lifemind.bluer.entity.Code;
import com.lifemind.bluer.entity.Etable;
import com.lifemind.bluer.entity.Result;
import com.lifemind.bluer.service.impl.EtableServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ckz
 * @since 2022-11-06
 */
@RestController
@RequestMapping("/etable")
public class EtableController {
    @Autowired
    private EtableServiceImpl etableService;

    @RequestMapping("/addTable")
    @ResponseBody
    public Result addNoteTable(@RequestBody Etable table) {
        boolean save = etableService.save(table);
        if (save) {
            HashMap<String, Object> hs = new HashMap<>();
            hs.put("etable", table);
            return new Result(hs, Code.SUCCESS, "");
        }
        return new Result(null, Code.SYSTEM_ERROR, "");
    }


    @DeleteMapping("/deleteNoteTable")
    @ResponseBody
    public Result deleteNoteTable(@RequestParam String etable_id) {
        boolean b = etableService.removeById(etable_id);
        if (b) {
            return new Result(null, Code.SUCCESS, "");
        }
        return new Result(null, Code.SYSTEM_ERROR, "");

    }

    @RequestMapping("/getTable")
    @ResponseBody
    public Result getTableInfo(@RequestParam String etable_id) {
        Etable byId = etableService.getById(etable_id);

        if (byId != null) {

            return new Result(null, Code.SUCCESS, "");

        } else {
            return new Result(null, Code.SYSTEM_ERROR, "");

        }
    }


    @RequestMapping("/addTableColum")
    @ResponseBody
    public Result addTableColum(@RequestBody List<String> colums, @RequestBody String et_id) {
        UpdateWrapper updateWrapper = new UpdateWrapper();
        String s = JSON.toJSONString(colums);
        updateWrapper.eq("table_id", et_id);
        updateWrapper.set("colum", s);
        boolean update = etableService.update(null, updateWrapper);
        if (update) {
            return new Result(null, Code.SUCCESS, "");
        }
        return new Result(null, Code.SYSTEM_ERROR, "");

    }


    @RequestMapping("/deleteTableColum")
    @ResponseBody
    public Result deleteTableColum(@RequestBody List<String> colums, @RequestBody String et_id) {
        UpdateWrapper updateWrapper = new UpdateWrapper();
        String s = JSON.toJSONString(colums);
        updateWrapper.eq("table_id", et_id);
        updateWrapper.set("colum", s);
        boolean update = etableService.update(null, updateWrapper);
        if (update) {
            return new Result(null, Code.SUCCESS, "");
        }
        return new Result(null, Code.SYSTEM_ERROR, "");

    }
}
