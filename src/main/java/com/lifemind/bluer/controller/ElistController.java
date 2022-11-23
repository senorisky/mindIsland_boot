package com.lifemind.bluer.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lifemind.bluer.entity.*;
import com.lifemind.bluer.service.impl.ElistServiceImpl;
import com.lifemind.bluer.uitls.VideoPoster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
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
     * 单列表上传图片或者视频
     *
     * @param userId
     * @param file
     * @return
     */
    @RequestMapping("/upload")
    @ResponseBody
    public Result UpLoadVideoOrOPic(
            @RequestParam String userId,
            @RequestParam Integer index,
            @RequestParam String elistString,
            @RequestParam MultipartFile file) {
        Elist elist = JSON.parseObject(elistString, Elist.class);
        File pichome = new File("/LifeMind/" + userId);
        if (!pichome.exists()) {
            pichome.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();
        String filePath = pichome.getAbsolutePath() + "\\" + fileName;
        try {
            System.out.println("上传的文件" + filePath);
            //将文件保存指定目录
            File newpic = new File(filePath);
            if (newpic.exists()) {
                return new Result(null, Code.File_Exist, "已有同名文件存在");
            }
            file.transferTo(newpic);
            if (!newpic.exists()) {
                return new Result(null, Code.File_Exist, "保存失败");
            }
            String fileUrl = "http://localhost:8081/LifeMind/" + userId + "/" + fileName;
            //保存视频成功 那么就多保存一张封面照片
            String postUrl = null;
            if (fileType.contains("video")) {
                postUrl = VideoPoster.fetchFrame(pichome.getAbsolutePath(), filePath, userId);
            }
            //存入数据库，
            List<ElistItem> datas = elist.getDatas();
            ElistItem elistItem = datas.get(0);
            List<JSONObject> items = elistItem.getItems();
            JSONObject jsonObject = items.get(index);
            jsonObject.put("url", fileUrl);
            jsonObject.put("poster", postUrl);
            items.set(index, jsonObject);
            elistItem.setItems(items);
            datas.set(0, elistItem);
            elist.setDatas(datas);
            elist.setData(JSON.toJSONString(elist.getDatas()));
            QueryWrapper wrapper = new QueryWrapper();
            wrapper.eq("view_id", elist.getViewId());
            boolean update = elistService.update(elist, wrapper);
            if (!update) {
                return new Result(null, Code.SYSTEM_ERROR, "上传失败,可能存在同名文件,请修改");
            } else {
                HashMap<String, Object> data = new HashMap<>();
                data.put("elist", elist);
                return new Result(data, Code.SUCCESS, "List更新成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(null, Code.SYSTEM_ERROR, "上传失败,可能存在同名图片,请修改");
        }
    }

    @RequestMapping("/downSinglePic")
    @ResponseBody
    public void downSinglePic(@RequestParam String userId,
                              @RequestParam String name,
                              HttpServletResponse response) {
        elistService.downResource(userId, name, response);

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
