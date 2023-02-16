package com.lifemind.bluer.controller;


import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lifemind.bluer.entity.Code;
import com.lifemind.bluer.entity.Gallery;
import com.lifemind.bluer.entity.Result;
import com.lifemind.bluer.service.impl.GalleryServiceImpl;
import com.lifemind.bluer.uitls.TokenUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ckz
 * @since 2022-11-06
 */
@RestController
@RequestMapping("/gallery")
public class GalleryController {

    @Autowired
    private GalleryServiceImpl galleryService;

    @RequestMapping("/getAllpic")
    @ResponseBody
    public Result getAllpic(@RequestParam String viewId, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("view_id", viewId);
        Gallery one = galleryService.getOne(wrapper);
        if (one != null) {
            List<JSONObject> list = JSON.parseArray(one.getData(), JSONObject.class);
            one.setDatas(list);
            HashMap<String, Object> data = new HashMap<>();
            data.put("gallery", one);
            return new Result(data, Code.SUCCESS, "图片读取成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "图片读取失败");
    }

    @RequestMapping("/downSinglePic")
    @ResponseBody
    public void downSinglePic(@RequestParam String userId,
                              @RequestParam String name,
                              @RequestParam String viewId,
                              HttpServletResponse response,
                              @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return;
        }
        galleryService.downPic(userId, name, viewId, response);

    }

    @Transactional
    @RequestMapping("/deleteOne")
    @ResponseBody
    public Result deleteOne(@RequestParam String userId,
                            @RequestParam String viewId,
                            @RequestParam String picName, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        File pichome = new File("/LifeMind/" + userId + "/" + viewId + "/" + picName);
        boolean exists = pichome.exists();
        System.out.println(picName);
        System.out.println(exists);
        if (exists) {
            Gallery gallery = galleryService.deleteOneGalleryPic(viewId, picName);
            if (gallery != null) {
                boolean delete = pichome.delete();
                System.out.println(delete);
                if (delete) {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("gallery", gallery);
                    return new Result(data, Code.SUCCESS, "删除图片成功");
                } else {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                }
            }
        }
        return new Result(null, Code.SYSTEM_ERROR, "删除失败");

    }

    @Transactional
    @RequestMapping("/upload")
    @ResponseBody
    public Result UpLoadPics(@RequestParam String viewId,
                             @RequestParam String userId,
                             @RequestParam MultipartFile file, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        File pichome = new File("/LifeMind/" + userId + "/" + viewId);
        if (!pichome.exists()) {
            pichome.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        String filePath = pichome.getAbsolutePath() + "/" + fileName;
        try {
            System.out.println(filePath);
            //将文件保存指定目录
            File newpic = new File(filePath);
            if (newpic.exists()) {
                return new Result(null, Code.File_Exist, "已有同名图片存在");
            }
            file.transferTo(newpic);
            String fileUrl = "http://localhost:8081/LifeMind/" + userId + "/" + viewId + "/" + fileName;
            //存入数据库，
            QueryWrapper wrapper = new QueryWrapper();
            wrapper.eq("view_id", viewId);
            Gallery one = galleryService.getOne(wrapper);
            List<JSONObject> list = JSON.parseArray(one.getData(), JSONObject.class);
            if (list == null) {
                list = new ArrayList<>();
            }
            JSONObject a = new JSONObject();
            a.put("name", fileName);
            a.put("url", fileUrl);
            list.add(a);
            one.setData(JSON.toJSONString(list));
            one.setDatas(list);
            boolean update = galleryService.update(one, wrapper);
            if (update) {
                HashMap<String, Object> data = new HashMap<>();
                data.put("name", fileName);
                data.put("url", fileUrl);
                return new Result(data, Code.SUCCESS, "上传成功");
            }
            return new Result(null, Code.SYSTEM_ERROR, "上传失败,可能存在同名图片,请修改");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(null, Code.SYSTEM_ERROR, "上传失败,可能存在同名图片,请修改");
        }
    }
}
