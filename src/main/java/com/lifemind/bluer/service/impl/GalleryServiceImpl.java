package com.lifemind.bluer.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lifemind.bluer.entity.Gallery;
import com.lifemind.bluer.mapper.GalleryMapper;
import com.lifemind.bluer.service.IGalleryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class GalleryServiceImpl extends ServiceImpl<GalleryMapper, Gallery> implements IGalleryService {

    @Autowired
    private GalleryMapper galleryMapper;

    @Override
    public Gallery deleteOneGalleryPic(String viewId, String name) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("view_id", viewId);
        Gallery gallery = galleryMapper.selectOne(wrapper);
        List<JSONObject> list = JSON.parseArray(gallery.getData(), JSONObject.class);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).get("name").equals(name)) {
                list.remove(i);
                break;
            }
        }
        gallery.setDatas(list);
        gallery.setData(JSON.toJSONString(list));
        int update = galleryMapper.update(gallery, wrapper);
        if (update == 1) {
            return gallery;
        }
        return null;
    }
}
