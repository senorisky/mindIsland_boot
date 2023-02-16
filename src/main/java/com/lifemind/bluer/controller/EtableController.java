package com.lifemind.bluer.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lifemind.bluer.entity.Code;
import com.lifemind.bluer.entity.Etable;
import com.lifemind.bluer.entity.Result;
import com.lifemind.bluer.service.impl.EtableServiceImpl;
import com.lifemind.bluer.uitls.TokenUtil;
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

    @RequestMapping("/saveTable")
    @ResponseBody
    public Result saveTable(@RequestBody Etable etable, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        String colum = JSON.toJSONString(etable.getColums());
        String data = JSON.toJSONString(etable.getDatas());
        etable.setColum(colum);
        etable.setData(data);
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("view_id", etable.getViewId());
        boolean save = etableService.update(etable, wrapper);
        if (save) {
            HashMap<String, Object> hs = new HashMap<>();
            hs.put("etable", etable);
            return new Result(hs, Code.SUCCESS, "保存表格成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "保存表格失败");
    }


    @DeleteMapping("/deleteNoteTable")
    @ResponseBody
    public Result deleteNoteTable(@RequestParam String etable_id, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        boolean b = etableService.removeById(etable_id);
        if (b) {
            return new Result(null, Code.SUCCESS, "删除Table成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "删除Table失败");

    }

    @RequestMapping("/getTable")
    @ResponseBody
    public Result getTableInfo(@RequestParam String viewId, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("view_id", viewId);
        Etable byId = etableService.getOne(wrapper);
        List<JSONObject> list = JSON.parseArray(byId.getData(), JSONObject.class);
        List<String> list1 = JSON.parseArray(byId.getColum(), String.class);
        byId.setDatas(list);
        byId.setColums(list1);
        if (byId != null) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("etable", byId);
            return new Result(data, Code.SUCCESS, "读取Table成功");

        } else {
            return new Result(null, Code.SYSTEM_ERROR, "读取Table失败");

        }
    }


    @RequestMapping("/addTableColumn")
    @ResponseBody
    public Result addTableColum(@RequestParam String name, @RequestParam String viewId, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        Etable etable = etableService.addColum(name, viewId);
        if (etable != null) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("etable", etable);
            return new Result(data, Code.SUCCESS, "表格添加列成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "表格添加列失败");

    }

    @RequestMapping("/addTableItem")
    @ResponseBody
    public Result addTableItem(@RequestParam String viewId, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        System.out.println(viewId);
        Etable etable = etableService.addItem(viewId);
        if (etable != null) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("etable", etable);
            return new Result(data, Code.SUCCESS, "表格添加列成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "表格添加列失败");

    }

    @RequestMapping("/deleteTableItem")
    @ResponseBody
    public Result deleteTableItem(@RequestParam Integer index, @RequestParam String viewId, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        Etable etable = etableService.deleteItem(index, viewId);
        if (etable != null) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("etable", etable);
            return new Result(data, Code.SUCCESS, "表格项删除列成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "表格项删除失败");

    }

    @RequestMapping("/deleteTableColum")
    @ResponseBody
    public Result deleteTableColum(@RequestParam String name, @RequestParam String viewId, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        Etable etable = etableService.deleteColum(name, viewId);
        if (etable != null) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("etable", etable);
            return new Result(data, Code.SUCCESS, "表格删除列成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "表格删除列失败");

    }
}
