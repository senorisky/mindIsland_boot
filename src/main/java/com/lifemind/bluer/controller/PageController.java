package com.lifemind.bluer.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lifemind.bluer.entity.*;
import com.lifemind.bluer.entity.Dto.PageItem;
import com.lifemind.bluer.service.PageService;
import com.lifemind.bluer.service.impl.PageServiceImpl;
import com.lifemind.bluer.uitls.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
    private PageService pageService;

    @RequestMapping("/savePageItemContent")
    @ResponseBody
    public Result savePageContent(@RequestParam String text, @RequestParam String noteId,
                                  @RequestParam Integer index, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        Page page = pageService.UpdateItemContent(noteId, text, index);
        if (page != null) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("page", page);
            return new Result(data, Code.SUCCESS, "保存文章成功");
        } else {
            return new Result(null, Code.SYSTEM_ERROR, "保存文章失败");
        }
    }

    @RequestMapping("/addPageContent")
    @ResponseBody
    public Result addPageContent(@RequestBody PageItem item, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
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
    public Result getPageData(@RequestParam String noteId, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
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

    @RequestMapping("/deletePageItem")
    @ResponseBody
    public Result deletePageItem(@RequestParam String noteId,
                                 @RequestParam Integer index,
                                 @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("note_id", noteId);
        System.out.println(noteId + index);
        Page one = pageService.getOne(wrapper);
        if (one != null) {
            List<JSONObject> datas = JSON.parseArray(one.getPageContent(), JSONObject.class);
            JSONObject remove = datas.remove(index.intValue());//使用remove的时候 按照下标删除  下标不能为i
            one.setDatas(datas);
            one.setPageContent(JSON.toJSONString(datas));
            String url = (String) remove.get("text");
            String path = "/www/wwwroot/" + url.substring(url.indexOf("LifeMind"));
            File file = new File(path);
            if (file.exists())
                file.delete();
            boolean update = pageService.update(one, wrapper);
            if (update) {
                HashMap<String, Object> data = new HashMap<>();
                data.put("page", one);
                return new Result(data, Code.SUCCESS, "读取文章成功");
            }
        }
        return new Result(null, Code.SYSTEM_ERROR, "删除元素失败");
    }

    @RequestMapping("/upload")
    @ResponseBody
    public Result UpLoadPic(@RequestParam String viewId,
                            @RequestParam String userId,
                            @RequestParam MultipartFile file, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        File pichome = new File("/www/wwwroot/LifeMind/" + userId + "/" + viewId);
        if (!pichome.exists()) {
            pichome.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        try {
            UUID uuid = UUID.randomUUID();
            String uid = uuid.toString();
            fileName = uid + fileName;
            String filePath = pichome.getAbsolutePath() + "/" + fileName;
            //将文件保存指定目录
            File newpic = new File(filePath);
            if (newpic.exists()) {
                return new Result(null, Code.File_Exist, "已有同名图片存在");
            }
            file.transferTo(newpic);
            String fileUrl = "http://*" + userId + "/" + viewId + "/" + fileName;
            HashMap<String, Object> data = new HashMap<>();
            data.put("name", fileName);
            data.put("url", fileUrl);
            return new Result(data, Code.SUCCESS, "上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(null, Code.SYSTEM_ERROR, "上传失败,可能存在同名图片,请修改");
        }
    }

    @RequestMapping("/changePic")
    @ResponseBody
    public Result changePic(@RequestParam String viewId,
                            @RequestParam String userId,
                            @RequestParam String lastUrl,
                            @RequestParam MultipartFile file, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        File pichome = new File("/www/wwwroot/LifeMind/" + userId + "/" + viewId);
        if (!pichome.exists()) {
            return new Result(null, Code.SYSTEM_ERROR, "系统错误");
        }
        String lastName = lastUrl.substring(lastUrl.lastIndexOf("/"));
        String fileName = file.getOriginalFilename();
        try {
            UUID uuid = UUID.randomUUID();
            String uid = uuid.toString();
            fileName = uid + fileName;
            String filePath = pichome.getAbsolutePath() + "/" + fileName;
            String lastPath = pichome.getAbsolutePath() + "/" + lastName;
            //将文件保存指定目录
            File newpic = new File(filePath);
            File lastPic = new File(lastPath);
            if (newpic.exists()) {
                return new Result(null, Code.File_Exist, "已有同名图片存在");
            }
            lastPic.deleteOnExit();
            file.transferTo(newpic);
            String fileUrl = "http://*" + userId + "/" + viewId + "/" + fileName;
            HashMap<String, Object> data = new HashMap<>();
            data.put("name", fileName);
            data.put("url", fileUrl);
            return new Result(data, Code.SUCCESS, "上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(null, Code.SYSTEM_ERROR, "上传失败,可能存在同名图片,请修改");
        }
    }


}
