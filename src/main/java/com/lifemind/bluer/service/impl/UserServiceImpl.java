package com.lifemind.bluer.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lifemind.bluer.entity.*;
import com.lifemind.bluer.entity.Dto.ListData;
import com.lifemind.bluer.entity.Dto.MenuData;
import com.lifemind.bluer.mapper.*;
import com.lifemind.bluer.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lifemind.bluer.uitls.MySecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ckz
 * @since 2022-11-06
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private PageMapper pageMapper;

    @Autowired
    private ViewMapper viewMapper;

    @Autowired
    private ElistMapper elistMapper;

    @Autowired
    private EtableMapper etableMapper;
    @Autowired
    private GalleryMapper galleryMapper;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public boolean regist(User user) {
        try {
            String dp = MySecurityUtil.desEncrypt(user.getPassword());
            String encode = passwordEncoder.encode(dp);
//            System.out.println(dp + "\n" + encode);
//            System.out.println(passwordEncoder.matches(dp, encode));
            user.setPassword(encode);
            user.setCreateTime(LocalDateTime.now());
            user.setLocked("N");
            user.setIsOnline("N");
            int i = userMapper.insert(user);
            if (i <= 0) {
                return false;
            }
            return defaultTemplateNotes(user.getUserId());
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public boolean deleteUser(String userId) {
        QueryWrapper wrapper = new QueryWrapper();
        QueryWrapper wrapper1 = new QueryWrapper();
        QueryWrapper wrapper2 = new QueryWrapper();
        List<String> nids = noteMapper.selectNoteIdListByUid(userId);
        if (nids.size() > 0) {
            QueryWrapper wrapper3 = new QueryWrapper();
            ;
            wrapper3.in("v.note_id", nids);
            List<String> views = viewMapper.selectViewIdListByNids(wrapper3);
            System.out.println("查询到views" + views);
            if (views.size() > 0) {
                wrapper.in("note_id", nids);
                wrapper1.in("view_id", views);
                elistMapper.delete(wrapper1);
                etableMapper.delete(wrapper1);
                galleryMapper.delete(wrapper1);
                int delete = viewMapper.delete(wrapper);
                pageMapper.delete(wrapper);
                if (delete < 0)
                    return false;
            }
            wrapper2.eq("user_id", userId);
            int delete = noteMapper.delete(wrapper2);
            if (delete < 0)
                return false;
        }
        Integer i = userMapper.deleteById(userId);
        return i == 1;
    }

    @Override
    public List<String> getUserPermissions(String principal) {

        return new ArrayList<>();
    }

    @Override
    public boolean checkExist(String email) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("email", email);
        try {
            return userMapper.selectOne(queryWrapper) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean defaultTemplateNotes(String user_id) {
        boolean f = defaultDailyNote(user_id);
        f = f && defaultRecipeNote(user_id);
        f = f && defaultReadingNote(user_id);
        f = f && defaultWorkNote(user_id);
        f = f && defaultPicNote(user_id);
        f = f && defaultVideoNote(user_id);
        f = f && defaultTravelNote(user_id);
        f = f && defaultPersonHome(user_id);
        return f;
    }

    private boolean defaultPersonHome(String user_id) {
        Note personHome = new Note();
        personHome.setName("Home");
        personHome.setFname("");
        personHome.setId(user_id + "Home");
        personHome.setType("note");
        personHome.setIcon("iconfont el-icon-setting");
        personHome.setInfo("个人中心");
        personHome.setCreateTime(LocalDateTime.now());
        personHome.setUserId(user_id);
        personHome.setComponent("PersonSet");
        personHome.setPath("");
        int save = noteMapper.insert(personHome);
        return save == 1;
    }

    private boolean defaultRecipeNote(String user_id) {
        Note RecipeNote = new Note();
        LocalDateTime localDateTime = LocalDateTime.now();
        RecipeNote.setName("Recipe");
        RecipeNote.setFname("");
        RecipeNote.setId(user_id + "Recipe");
        RecipeNote.setType("note");
        RecipeNote.setIcon("iconfont el-icon-home");
        RecipeNote.setInfo("这是一则食谱，你可以用自己喜欢的形式记录一些美味佳肴");
        RecipeNote.setCreateTime(LocalDateTime.now());
        RecipeNote.setUserId(user_id);
        RecipeNote.setComponent("NoteView");
        RecipeNote.setPath("");
        View recipeList = new View();
        recipeList.setNoteId(RecipeNote.getId());
        recipeList.setFname(RecipeNote.getName());
        recipeList.setIcon("iconfont el-icon-dian1");
        recipeList.setId(RecipeNote.getId() + "recipe");
        recipeList.setInfo("");
        recipeList.setName("List");
        recipeList.setCreateTime(localDateTime);
        recipeList.setComponent("ListView");
        recipeList.setPath("");
        recipeList.setType("view");
        ArrayList<ListData> list = new ArrayList<>();
        ListData item = new ListData("Want to eat");
        list.add(item);
        String data = JSON.toJSONString(list);
        Elist elist = new Elist();
        elist.setViewId(recipeList.getId());
        elist.setData(data);
        int save = noteMapper.insert(RecipeNote);
        int i = viewMapper.insert(recipeList);
        int d = elistMapper.insert(elist);
        return (save == 1 && i == 1 && d == 1);
    }

    private boolean defaultDailyNote(String user_id) {
        Note DailyNote = new Note();
        LocalDateTime localDateTime = LocalDateTime.now();
        DailyNote.setName("Diary");
        DailyNote.setFname("");
        DailyNote.setId(user_id + "Diary");
        DailyNote.setIcon("iconfont el-icon-miao");
        DailyNote.setInfo("这是一则日记，你可以记录你的美好生活中的点滴。");
        DailyNote.setCreateTime(LocalDateTime.now());
        DailyNote.setType("note");
        DailyNote.setComponent("NoteView");
        DailyNote.setPath("");
        DailyNote.setUserId(user_id);
        View dailyList = new View();
        dailyList.setId(DailyNote.getId() + "daily");
        dailyList.setNoteId(DailyNote.getId());
        dailyList.setCreateTime(localDateTime);
        dailyList.setName("DailyList");
        dailyList.setFname(DailyNote.getName());
        dailyList.setPath("");
        dailyList.setInfo("");
        dailyList.setIcon("iconfont el-icon-dian1");
        dailyList.setType("view");
        dailyList.setComponent("sListView");
        ListData listData = new ListData("daily");
        ArrayList<ListData> list = new ArrayList<>();
        list.add(listData);
        String json = JSON.toJSONString(list);
        Elist elist = new Elist();
        elist.setViewId(dailyList.getId());
        elist.setData(json);
        int save = noteMapper.insert(DailyNote);
        int i = viewMapper.insert(dailyList);
        int d = elistMapper.insert(elist);
        return (save == 1 && i == 1 && d == 1);
    }

    private boolean defaultWorkNote(String user_id) {
        Note WorkNote = new Note();
        LocalDateTime localDateTime = LocalDateTime.now();
        WorkNote.setId(user_id + "Task");
        WorkNote.setType("note");
        WorkNote.setIcon("iconfont el-icon-gongzuotai");
        WorkNote.setInfo("这是一则任务清单日志，你可以记录你所有的任务，并且为这些任务设置状态，或者添加日历来直观的感受你的任务分布。");
        WorkNote.setCreateTime(LocalDateTime.now());
        WorkNote.setName("Task");
        WorkNote.setFname("");
        WorkNote.setComponent("NoteView");
        WorkNote.setPath("");
        WorkNote.setUserId(user_id);
        View workList = new View();
        workList.setId(WorkNote.getId() + "work");
        workList.setNoteId(WorkNote.getId());
        workList.setCreateTime(localDateTime);
        workList.setIcon("iconfont el-icon-dian1");
        workList.setInfo("");
        workList.setCreateTime(localDateTime);
        workList.setFname(WorkNote.getName());
        workList.setName("workList");
        workList.setPath("");
        workList.setType("view");
        workList.setComponent("ListView");
        int save = noteMapper.insert(WorkNote);
        int i = viewMapper.insert(workList);
        ArrayList<ListData> items = new ArrayList<>();
        ListData listData = new ListData("Todo");
        ListData listData2 = new ListData("Doing");
        ListData listData3 = new ListData("Done");
        items.add(listData);
        items.add(listData2);
        items.add(listData3);
        Elist elist = new Elist();
        elist.setViewId(workList.getId());
        elist.setData(JSON.toJSONString(items));
        int d = elistMapper.insert(elist);
        return (save == 1 && i == 1 && d == 1);
    }

    private boolean defaultReadingNote(String user_id) {
        Note readingNote = new Note();
        LocalDateTime localDateTime = LocalDateTime.now();
        readingNote.setId(user_id + "Reading");
        readingNote.setType("note");
        readingNote.setIcon("iconfont el-icon-dushu");
        readingNote.setInfo("这是一则阅读日志，你可以记录你每次阅读后的感受，或者添加List记录你的阅读计划，" +
                "或者添加画廊记录美好的插图等等");
        readingNote.setCreateTime(LocalDateTime.now());
        readingNote.setUserId(user_id);
        readingNote.setComponent("NoteView");
        readingNote.setName("Reading");
        readingNote.setPath("");
        readingNote.setFname("");
        View readList = new View();
        readList.setId(readingNote.getId() + "reading");
        readList.setNoteId(readingNote.getId());
        readList.setCreateTime(localDateTime);
        readList.setType("view");
        readList.setName("readList");
        readList.setInfo("");
        readList.setFname(readingNote.getName());
        readList.setIcon("iconfont el-icon-dian1");
        readList.setPath("");
        readList.setComponent("ListView");
        int save = noteMapper.insert(readingNote);
        int i = viewMapper.insert(readList);
        ArrayList<ListData> list = new ArrayList<>();
        ListData item = new ListData("Reading");
        ListData item2 = new ListData("ReadOver");
        list.add(item);
        list.add(item2);
        Elist elist = new Elist();
        elist.setData(JSON.toJSONString(list));
        elist.setViewId(readList.getId());
        int d = elistMapper.insert(elist);
        return (save == 1 && i == 1 && d == 1);
    }

    private boolean defaultPicNote(String user_id) {
        Note gallery = new Note();
        gallery.setUserId(user_id);
        gallery.setName("Gallery");
        gallery.setFname("");
        gallery.setIcon("iconfont el-icon-tuku");
        gallery.setPath("");
        gallery.setComponent("NoteView");
        gallery.setType("note");
        gallery.setInfo("这是一则画廊日志，你可以上传图片，并且为每一个图片添加信息,一个Gallery最多保存200张图片。");
        LocalDateTime localDateTime = LocalDateTime.now();
        gallery.setCreateTime(localDateTime);
        gallery.setId(user_id + "Gallery");
        View galleryTable = new View();
        galleryTable.setNoteId(gallery.getId());
        galleryTable.setPath("");
        galleryTable.setId(gallery.getId() + "pictures");
        galleryTable.setInfo("");
        galleryTable.setFname(gallery.getName());
        galleryTable.setCreateTime(localDateTime);
        galleryTable.setIcon("iconfont el-icon-dian1");
        galleryTable.setType("view");
        galleryTable.setName("Gallery");
        galleryTable.setComponent("GalleryView");
        int save = noteMapper.insert(gallery);
        int i = viewMapper.insert(galleryTable);
        Gallery gdata = new Gallery();
        gdata.setData("");
        gdata.setViewId(galleryTable.getId());
        int d = galleryMapper.insert(gdata);
        return (save == 1 && i == 1 && d == 1);
    }

    private boolean defaultVideoNote(String user_id) {
        Note videos = new Note();
        videos.setUserId(user_id);
        videos.setName("Video");
        videos.setPath("");
        videos.setComponent("NoteView");
        videos.setFname("");
        videos.setIcon("iconfont el-icon-shipin");
        videos.setType("note");
        videos.setInfo("这是一个视频集日志，你可以将一些小型视频上传、编写你的感想");
        LocalDateTime localDateTime = LocalDateTime.now();
        videos.setCreateTime(localDateTime);
        videos.setId(user_id + "Video");
        View videoList = new View();
        videoList.setId(videos.getId() + "video");
        videoList.setNoteId(videos.getId());
        videoList.setType("view");
        videoList.setComponent("sListView");
        videoList.setFname(videos.getName());
        videoList.setInfo("");
        videoList.setIcon("iconfont el-icon-dian1");
        videoList.setCreateTime(localDateTime);
        videoList.setName("videoList");
        videoList.setPath("");
        ListData item = new ListData("Films");
        ArrayList<ListData> list = new ArrayList<>();
        list.add(item);
        Elist elist = new Elist();
        elist.setData(JSON.toJSONString(list));
        elist.setViewId(videoList.getId());
        int save = noteMapper.insert(videos);
        int i = viewMapper.insert(videoList);
        int d = elistMapper.insert(elist);
        return (save == 1 && i == 1 && d == 1);
    }

    private boolean defaultTravelNote(String user_id) {
        Note travelNote = new Note();
        travelNote.setUserId(user_id);
        travelNote.setName("Travel");
        travelNote.setFname("");
        travelNote.setPath("");
        travelNote.setComponent("NoteView");
        travelNote.setIcon("iconfont el-icon-feiji");
        travelNote.setType("note");
        travelNote.setInfo("这是一则旅游日志，你可以在这里记录你的每一次旅途，或者添加List来规划你的旅途");
        LocalDateTime localDateTime = LocalDateTime.now();
        travelNote.setCreateTime(localDateTime);
        travelNote.setId(user_id + "Travel");
        View travelTable = new View();
        travelTable.setNoteId(travelNote.getId());
        travelTable.setId(travelNote.getId() + "table");
        travelTable.setInfo("");
        travelTable.setPath("");
        travelTable.setCreateTime(localDateTime);
        travelTable.setName("TravelTable");
        travelTable.setType("view");
        travelTable.setFname(travelNote.getName());
        travelTable.setComponent("TableView");
        travelTable.setIcon("iconfont el-icon-dian1");
        ArrayList<String> colums = new ArrayList<>();
        List<JSONObject> data = new ArrayList<>();
        colums.add("name");
        colums.add("time");
        colums.add("place");
        colums.add("people");
        View travelGallery = new View();
        travelGallery.setNoteId(travelNote.getId());
        travelGallery.setPath("");
        travelGallery.setId(travelNote.getId() + "gallery");
        travelGallery.setInfo("");
        travelGallery.setFname(travelNote.getName());
        travelGallery.setCreateTime(localDateTime);
        travelGallery.setIcon("iconfont el-icon-dian");
        travelGallery.setType("view");
        travelGallery.setName("Gallery");
        travelGallery.setComponent("GalleryView");
        Etable etable = new Etable();
        etable.setViewId(travelTable.getId());
        etable.setColums(colums);
        etable.setColum(JSON.toJSONString(colums));
        int save = noteMapper.insert(travelNote);
        int i = viewMapper.insert(travelTable);
        int t = viewMapper.insert(travelGallery);
        int d = etableMapper.insert(etable);
        return (save == 1 && i == 1 && t == 1 && d == 1);
    }

}
