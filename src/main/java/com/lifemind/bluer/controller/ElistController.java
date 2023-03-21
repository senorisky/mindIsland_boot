package com.lifemind.bluer.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lifemind.bluer.entity.*;
import com.lifemind.bluer.service.IElistService;
import com.lifemind.bluer.service.impl.ElistServiceImpl;
import com.lifemind.bluer.uitls.TokenUtil;
import com.lifemind.bluer.uitls.VideoPoster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private IElistService elistService;

    /**
     * 获得当前EList数据
     *
     * @param viewId
     * @return
     */
    @RequestMapping("/getEList")
    @ResponseBody
    public Result getEList(@RequestParam String viewId, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
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
            @RequestParam MultipartFile file, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        Elist update = elistService.UpLoadResource(userId, index, elistString, file);
        if (update == null) {
            return new Result(null, Code.SYSTEM_ERROR, "上传失败,可能存在同名文件,请修改");
        } else {
            HashMap<String, Object> data = new HashMap<>();
            data.put("elist", update);
            return new Result(data, Code.SUCCESS, "List更新成功");
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
     * @param data
     * @return
     */
    @Transactional
    @RequestMapping("/saveEList")
    @ResponseBody
    public Result saveListItems(@RequestBody Map<String, Object> data, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        String elistStr = JSON.toJSONString(data.get("elist"));
        String url = (String) data.get("url");
        Elist elist = JSON.parseObject(elistStr, Elist.class);
        System.out.println(elistStr);
        elist.setData(JSON.toJSONString(elist.getDatas()));
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("view_id", elist.getViewId());
        boolean update = elistService.update(elist, wrapper);
        if (!update) {
            return new Result(null, Code.SYSTEM_ERROR, "List更新失败");
        } else {
            if (!"".equals(url)) {

                int i = url.indexOf("LifeMind");
                String path = "/www/wwwroot/" + url.substring(i);
                File file = new File(path);
                if (!file.exists()) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("elist", elist);
                    return new Result(null, Code.SUCCESS, "资源错误,请联系开发者");
                }
                boolean delete = file.delete();
                if (!delete) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return new Result(null, Code.SYSTEM_ERROR, "List更新失败");
                }
                String type = url.substring(url.lastIndexOf("."));
                if (type.equals(".mp4")) {//还要删除封面
                    String poster = (String) data.get("poster");
                    int j = poster.indexOf("LifeMind");
                    String p = "/www/wwwroot/" + poster.substring(j);
                    File pfile = new File(p);
                    if (!pfile.exists()) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("elist", elist);
                        return new Result(null, Code.SUCCESS, "资源错误,请联系开发者");
                    }
                    boolean posterd = pfile.delete();
                    if (!posterd) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return new Result(null, Code.SYSTEM_ERROR, "List更新失败");
                    }
                }
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("elist", elist);
            return new Result(map, Code.SUCCESS, "List更新成功");
        }
    }
}
