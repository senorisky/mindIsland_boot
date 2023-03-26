package com.lifemind.bluer.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lifemind.bluer.entity.Code;
import com.lifemind.bluer.entity.Elist;
import com.lifemind.bluer.entity.ElistItem;
import com.lifemind.bluer.entity.Result;
import com.lifemind.bluer.mapper.ElistMapper;
import com.lifemind.bluer.service.IElistService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lifemind.bluer.uitls.VideoPoster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ckz
 * @since 2022-11-06
 */
@Service
public class ElistServiceImpl extends ServiceImpl<ElistMapper, Elist> implements IElistService {

    @Autowired
    private ElistMapper elistMapper;

    @Override
    public void downResource(String userId, String name, HttpServletResponse response) {
        File file = new File("/www/wwwroot/LifeMind/" + userId + "/" + name);
        System.out.println(file);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ServletOutputStream outputStream = response.getOutputStream();
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            response.setContentType("video/mp4");
            //关闭资源
            outputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Elist UpLoadResource(String userId, Integer index, String elistString, MultipartFile file) {
        Elist elist = JSON.parseObject(elistString, Elist.class);
        File pichome = new File("/www/wwwroot/LifeMind/" + userId);
        if (!pichome.exists()) {
            pichome.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();
        String filePath = pichome.getAbsolutePath() + "/" + fileName;
        try {
            System.out.println("上传的文件" + filePath);
            //将文件保存指定目录
            File newpic = new File(filePath);
            if (newpic.exists()) {
                return null;
            }
            file.transferTo(newpic);
            if (!newpic.exists()) {
                return null;
            }
            String fileUrl = "http://*" + userId + "/" + fileName;
            //保存视频成功 那么就多保存一张封面照片
            String postUrl = null;
            if (fileType.contains("video")) {
                //获取图片
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
            int update = elistMapper.update(elist, wrapper);
            if (update == 1)
                return elist;
            else
                return null;
        } catch (Exception e) {
            return null;
        }
    }
}
