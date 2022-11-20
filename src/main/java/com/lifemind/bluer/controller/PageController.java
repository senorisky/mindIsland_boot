package com.lifemind.bluer.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lifemind.bluer.entity.Code;
import com.lifemind.bluer.entity.Dto.PageItem;
import com.lifemind.bluer.entity.Page;
import com.lifemind.bluer.entity.Result;
import com.lifemind.bluer.service.impl.PageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * @Classname PageController
 * @Description TODO
 * @Date 2022/11/14 9:50
 * @Created by senorisky
 */
@RestController
@RequestMapping("/page")
public class PageController {
    @Autowired
    private PageServiceImpl pageService;

    @RequestMapping("/savePageContent")
    @ResponseBody
    public Result savePageContent(@RequestBody Page page) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("note_id", page.getNoteId());
        page.setPageContent(JSON.toJSONString(page.getDatas()));
        System.out.println(page);
        boolean update = pageService.update(page, wrapper);
        if (update) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("page", page);
            return new Result(data, Code.SUCCESS, "保存文章成功");
        } else {
            return new Result(null, Code.SYSTEM_ERROR, "保存文章失败");
        }
    }

    @RequestMapping("/addPageContent")
    @ResponseBody
    public Result addPageContent(@RequestBody PageItem item) {
        System.out.println(item);
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("note_id", item.getPageId());
        Page one = pageService.getOne(wrapper);
        if (one == null) {
            return new Result(null, Code.SYSTEM_ERROR, "系统错误");
        }
        List<JSONObject> list = JSON.parseArray(one.getPageContent(), JSONObject.class);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", item.getName());
        jsonObject.put("text", item.getText());
        list.add(jsonObject);
        one.setPageContent(JSONObject.toJSONString(list));
        boolean update = pageService.update(one, wrapper);
        if (update) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("pageData", one);
            return new Result(data, Code.SUCCESS, "文章添加文本框成功");
        } else {
            return new Result(null, Code.SYSTEM_ERROR, "文章添加文本框失败");
        }
    }

    @RequestMapping("/getPageData")
    @ResponseBody
    public Result getPageData(@RequestParam String noteId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("note_id", noteId);
        Page one = pageService.getOne(wrapper);
        if (one != null) {
            HashMap<String, Object> data = new HashMap<>();
            List<JSONObject> list = JSON.parseArray(one.getPageContent(), JSONObject.class);
            one.setDatas(list);
            data.put("page", one);
            return new Result(data, Code.SUCCESS, "读取文章成功");
        } else {
            return new Result(null, Code.SYSTEM_ERROR, "读取文章失败");
        }
    }
}
