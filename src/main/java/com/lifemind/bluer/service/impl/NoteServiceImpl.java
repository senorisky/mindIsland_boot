package com.lifemind.bluer.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lifemind.bluer.entity.Note;
import com.lifemind.bluer.entity.Page;
import com.lifemind.bluer.entity.User;
import com.lifemind.bluer.entity.View;
import com.lifemind.bluer.mapper.*;
import com.lifemind.bluer.service.INoteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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

    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private ViewMapper viewMapper;
    @Autowired
    private ElistMapper elistMapper;
    @Autowired
    private EtableMapper etableMapper;
    @Autowired
    private GalleryMapper galleryMapper;
    @Autowired
    private UserMapper userMapper;

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

    @Override
    public boolean removeNote(String note_id, String userId) throws IOException {
        QueryWrapper wrapper = new QueryWrapper();
        QueryWrapper wrapper1 = new QueryWrapper();
        QueryWrapper wrapper2 = new QueryWrapper();
        QueryWrapper wrapper3 = new QueryWrapper();
        wrapper.eq("user_id", userId);
        User user = userMapper.selectOne(wrapper);
        if (user != null) {
            List<String> views = viewMapper.selectViewIdByNid(note_id);
//            System.out.println("getViewIds" + views);
            wrapper2.eq("note_id", note_id);
            if (views.size() > 0) {
                wrapper1.in("view_id", views);
                elistMapper.delete(wrapper1);
                etableMapper.delete(wrapper1);
                galleryMapper.delete(wrapper1);
                int delete = viewMapper.delete(wrapper2);
                if (delete < 0)
                    return false;
            }
            pageMapper.delete(wrapper2);
            //删除view的所有文件夹
            for (String i : views) {
                //删除资源
                File viewHome = new File("/www/wwwroot/LifeMind/" + userId + "/" + i);
                if (viewHome.exists()) {
                    FileUtils.deleteDirectory(viewHome);
                }
            }
            System.out.println("deleteNote" + note_id);
            wrapper3.eq("id", note_id);
            int delete = noteMapper.delete(wrapper3);
            return delete > 0;
        } else {
            return false;
        }
    }

    @Override
    public boolean removePage(String note_id, String userId) throws IOException {

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        User user = userMapper.selectOne(wrapper);
        if (user != null) {
            //先删除Page数据库中的数据
            QueryWrapper<Page> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("note_id", note_id);
            int delete = pageMapper.delete(wrapper1);
            if (delete == 1) {
                QueryWrapper<Note> wrapper2 = new QueryWrapper<>();
                //再删除Note中的page信息
                wrapper2.clear();
                wrapper2.eq("id", note_id);
                wrapper2.eq("type", "page");
                wrapper2.eq("user_id", userId);
                int delete1 = noteMapper.delete(wrapper2);
                if (delete1 == 1) {
                    //最后删除文件
                    File pageHome = new File("/www/wwwroot/LifeMind/" + userId + "/" + note_id);
                    if (pageHome.exists()) {
                        pageHome.delete();
                    }
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public List<Note> getMenuData(String uid) {
        QueryWrapper wrapper2 = new QueryWrapper();
        wrapper2.orderByAsc("create_time");
        wrapper2.eq("user_id", uid);
        List<Note> notes = noteMapper.selectList(wrapper2);
        for (int i = 0; i < notes.size(); i++) {
            String nid = notes.get(i).getId();
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("note_id", nid);
            List<View> views = viewMapper.selectList(queryWrapper);
            notes.get(i).setChildren(views);
        }
        return notes;
    }
}
