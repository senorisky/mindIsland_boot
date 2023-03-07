package com.lifemind.bluer.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lifemind.bluer.entity.*;
import com.lifemind.bluer.mapper.ElistMapper;
import com.lifemind.bluer.mapper.EtableMapper;
import com.lifemind.bluer.mapper.GalleryMapper;
import com.lifemind.bluer.mapper.ViewMapper;
import com.lifemind.bluer.service.IViewService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
public class ViewServiceImpl extends ServiceImpl<ViewMapper, View> implements IViewService {

    @Autowired
    private ElistMapper elistMapper;

    @Autowired
    private EtableMapper etableMapper;
    @Autowired
    private GalleryMapper galleryMapper;

    @Override
    public boolean InitView(View view) {
        if (("ListView".equals(view.getComponent())) || ("sListView".equals(view.getComponent()))) {
            Elist elist = new Elist();
            List<ElistItem> list = new ArrayList<>();
            ElistItem elistItem = new ElistItem();
            elistItem.setItems(new ArrayList<>());
            elistItem.setColum(view.getName());
            System.out.println(elistItem);
            list.add(elistItem);
            elist.setViewId(view.getId());
            elist.setData(JSON.toJSONString(list));
            int insert = elistMapper.insert(elist);
            return insert == 1;
        } else if ("TableView".equals(view.getComponent())) {
            Etable etable = new Etable();
            LocalDateTime localDateTime = LocalDateTime.now();
            String s = localDateTime.format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss"));
            List<String> columns = new ArrayList<>();
            List<JSONObject> data = new ArrayList<>();
            JSONObject object = new JSONObject();
            object.put("name", "default");
            object.put("time", s);
            data.add(object);
            columns.add("name");
            columns.add("time");
            etable.setViewId(view.getId());
            etable.setColum(JSON.toJSONString(columns));
            etable.setData(JSON.toJSONString(data));
            int insert = etableMapper.insert(etable);
            return insert == 1;
        } else if ("GalleryView".equals(view.getComponent())) {
            Gallery gallery = new Gallery();
            gallery.setViewId(view.getId());
            int insert = galleryMapper.insert(gallery);
            return insert == 1;
        } else if ("videoList".equals(view.getComponent())) {
            Elist elist = new Elist();
            List<ElistItem> list = new ArrayList<>();
            ElistItem elistItem = new ElistItem();
            elistItem.setItems(new ArrayList<>());
            elistItem.setColum(view.getName());
            System.out.println(elistItem);
            list.add(elistItem);
            elist.setViewId(view.getId());
            elist.setData(JSON.toJSONString(list));
            int insert = elistMapper.insert(elist);
            return insert == 1;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeViewData(View view, String userId) throws IOException {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("view_id", view.getId());
        String c = view.getComponent();
        if ("ListView".equals(c) || "sListView".equals(c)) {
            int delete = elistMapper.delete(wrapper);
            //删除资源
            File viewHome = new File("/www/wwwroot/LifeMind/" + userId + "/" + view.getId());
            if (viewHome.exists()) {
                FileUtils.deleteDirectory(viewHome);
            }
            return delete >= 1;
        } else if ("TableView".equals(c)) {
            int delete = etableMapper.delete(wrapper);
            return delete >= 1;
        } else if ("GalleryView".equals(c)) {
            int delete = galleryMapper.delete(wrapper);
            return delete >= 1;
        } else {
            return false;
        }

    }
}
