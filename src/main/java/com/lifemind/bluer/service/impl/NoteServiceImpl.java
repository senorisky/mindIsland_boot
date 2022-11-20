package com.lifemind.bluer.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lifemind.bluer.entity.Note;
import com.lifemind.bluer.entity.Page;
import com.lifemind.bluer.mapper.NoteMapper;
import com.lifemind.bluer.mapper.PageMapper;
import com.lifemind.bluer.service.INoteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements INoteService {

    @Autowired
    private PageMapper pageMapper;

    @Override
    public boolean InitDefaultPage(String nid) {
        Page page = new Page();
        page.setNoteId(nid);
        page.setImgPath("");
        List<JSONObject> data = new ArrayList<>();
        JSONObject a = new JSONObject();
        a.put("name", "HeadOne");
        a.put("text", "");
        JSONObject b = new JSONObject();
        b.put("name", "TextArea");
        b.put("text", "");
        data.add(a);
        data.add(b);
        page.setDatas(data);
        page.setPageContent(JSON.toJSONString(data));
        int insert = pageMapper.insert(page);
        return insert == 1;
    }
}
