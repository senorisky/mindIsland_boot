package com.lifemind.bluer.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lifemind.bluer.entity.Page;
import com.lifemind.bluer.service.PageService;
import com.lifemind.bluer.mapper.PageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author senorisky
 * @description 针对表【page】的数据库操作Service实现
 * @createDate 2022-11-14 09:43:42
 */
@Service
public class PageServiceImpl extends ServiceImpl<PageMapper, Page> implements PageService {

    @Autowired
    private PageMapper pageMapper;

    @Override
    public Page UpdateItemContent(String noteId, String text, Integer index) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("note_id", noteId);
        Page page = pageMapper.selectOne(wrapper);
        if (page == null) {
            return null;
        }
        List<JSONObject> list = JSON.parseArray(page.getPageContent(), JSONObject.class);
        JSONObject jsonObject = list.get(index.intValue());
        jsonObject.put("text", text);
        list.set(index.intValue(), jsonObject);
        page.setDatas(list);
        page.setPageContent(JSON.toJSONString(list));
        int update = pageMapper.update(page, wrapper);
        if (update != 1) {
            return null;
        }
        return page;
    }
}
