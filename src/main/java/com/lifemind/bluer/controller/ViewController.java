package com.lifemind.bluer.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lifemind.bluer.entity.Code;
import com.lifemind.bluer.entity.Result;
import com.lifemind.bluer.entity.View;
import com.lifemind.bluer.service.impl.ViewServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ckz
 * @since 2022-11-06
 */
@RestController
@RequestMapping("/view")
public class ViewController {

    @Autowired
    private ViewServiceImpl viewService;

    @Transactional
    @RequestMapping("/addView")
    @ResponseBody
    public Result addView(@RequestBody View view) {
        System.out.println(view);
        view.setCreateTime(LocalDateTime.now());
        boolean save = viewService.save(view);
        if (save) {
            boolean b = viewService.InitView(view);
            if (!b) {
                //回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new Result(null, Code.SYSTEM_ERROR, "添加view失败");
            }
            return new Result(null, Code.SUCCESS, "添加view成功");
        } else {
            return new Result(null, Code.SYSTEM_ERROR, "添加view失败");
        }
    }

    @RequestMapping("/saveView")
    @ResponseBody
    public Result saveView(@RequestBody View view) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("id", view.getId());
        boolean save = viewService.update(view, wrapper);
        if (save) {
            return new Result(null, Code.SUCCESS, "更新view成功");
        } else {
            return new Result(null, Code.SYSTEM_ERROR, "更新view失败");
        }
    }

    @RequestMapping("/deleteView")
    @ResponseBody
    public Result deleteView(@RequestBody View view) {
        QueryWrapper wrapper = new QueryWrapper();
        System.out.println(view);
        wrapper.eq("id", view.getId());
        boolean save = viewService.remove(wrapper);
        if (save) {
            viewService.removeViewData(view);
            return new Result(null, Code.SUCCESS, "删除view成功");
        } else {
            return new Result(null, Code.SYSTEM_ERROR, "删除view失败");
        }
    }
}