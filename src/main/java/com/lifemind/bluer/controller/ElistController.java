package com.lifemind.bluer.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lifemind.bluer.entity.Code;
import com.lifemind.bluer.entity.Elist;
import com.lifemind.bluer.entity.ElistItem;
import com.lifemind.bluer.entity.Result;
import com.lifemind.bluer.service.impl.ElistServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
@RequestMapping("/elist")
public class ElistController {
    @Autowired
    private ElistServiceImpl elistService;

    /**
     * 获得当前EList数据
     *
     * @param viewId
     * @return
     */
    @RequestMapping("/getEList")
    @ResponseBody
    public Result getEList(@RequestParam String viewId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("view_id", viewId);
        Elist one = elistService.getOne(wrapper);
        System.out.println(one.getData());
        List<ElistItem> elistItems = JSON.parseArray(one.getData(), ElistItem.class);
        one.setDatas(elistItems);
        HashMap<String, Object> data = new HashMap<>();
        data.put("elist", one);
        return new Result(data, Code.SUCCESS, "读取elist成功");
    }

    /**
     * 更新EList的数据
     *
     * @param elist
     * @return
     */
    @RequestMapping("/saveEList")
    @ResponseBody
    public Result saveListItems(@RequestBody Elist elist) {
        System.out.println(elist);
        elist.setData(JSON.toJSONString(elist.getDatas()));
        System.out.println(elist);
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("view_id", elist.getViewId());
        boolean update = elistService.update(elist, wrapper);
        if (!update) {
            return new Result(null, Code.SYSTEM_ERROR, "List更新失败");
        } else {
            HashMap<String, Object> data = new HashMap<>();
            data.put("elist", elist);
            return new Result(data, Code.SUCCESS, "List更新成功");
        }
    }
}
