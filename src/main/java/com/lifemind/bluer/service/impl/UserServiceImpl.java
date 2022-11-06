package com.lifemind.bluer.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lifemind.bluer.entity.*;
import com.lifemind.bluer.entity.Dto.ListData;
import com.lifemind.bluer.entity.Dto.MenuData;
import com.lifemind.bluer.mapper.*;
import com.lifemind.bluer.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
    private ViewMapper viewMapper;

    @Autowired
    private ElistMapper elistMapper;

    @Autowired
    private EtableMapper etableMapper;
    @Autowired
    private GalleryMapper galleryMapper;

    @Override
    public boolean regist(User user) {
        try {
            String id = user.getUserName();
            user.setUserId(id);
            user.setCreateTime(LocalDateTime.now());
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
        List<Note> notes = userMapper.selectNotes(uid);

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
        return f;
    }

    private boolean defaultRecipeNote(String user_id) {
        Note RecipeNote = new Note();
        LocalDateTime localDateTime = LocalDateTime.now();
        String s = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
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
        recipeList.setIcon("iconfont el-icon-dian");
        recipeList.setId(RecipeNote.getId() + s);
        recipeList.setInfo("");
        recipeList.setName("List");
        recipeList.setCreateTime(localDateTime);
        recipeList.setComponent("ListView");
        recipeList.setPath("");
        recipeList.setType("view");
        ArrayList<ListData> list = new ArrayList<>();
        ListData item = new ListData();
        item.setColum("Recipe");
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
        String s = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        DailyNote.setName("Diary");
        DailyNote.setFname("");
        DailyNote.setId(user_id + "Diary" + s);
        DailyNote.setIcon("iconfont el-icon-miao");
        DailyNote.setInfo("这是一则日记，你可以记录你的美好生活中的点滴。");
        DailyNote.setCreateTime(LocalDateTime.now());
        DailyNote.setType("note");
        DailyNote.setComponent("NoteView");
        DailyNote.setPath("");
        DailyNote.setUserId(user_id);
        View dailyList = new View();
        dailyList.setId(DailyNote.getId() + s);
        dailyList.setNoteId(DailyNote.getId());
        dailyList.setCreateTime(localDateTime);
        dailyList.setName("workList");
        dailyList.setFname(DailyNote.getName());
        dailyList.setPath("");
        dailyList.setInfo("");
        dailyList.setIcon("iconfont el-icon-dian");
        dailyList.setType("view");
        dailyList.setComponent("ListView");
        ListData listData = new ListData();
        listData.setColum("name");
        ArrayList<ListData> list = new ArrayList<>();
        list.add(listData);
        String json = JSON.toJSONString(listData);
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
        String s = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
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
        workList.setId(WorkNote.getId() + s);
        workList.setNoteId(WorkNote.getId());
        workList.setCreateTime(localDateTime);
        workList.setIcon("iconfont el-icon-dian");
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
        ListData listData = new ListData();
        listData.setColum("Todo");
        ListData listData2 = new ListData();
        listData2.setColum("Doing");
        ListData listData3 = new ListData();
        listData3.setColum("Done");
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
        String s = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
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
        readList.setId(readingNote.getId() + s);
        readList.setNoteId(readingNote.getId());
        readList.setCreateTime(localDateTime);
        readList.setType("view");
        readList.setName("readList");
        readList.setInfo("");
        readList.setFname(readingNote.getName());
        readList.setIcon("iconfont el-icon-dian");
        readList.setPath("");
        readList.setComponent("ListView");
        int save = noteMapper.insert(readingNote);
        int i = viewMapper.insert(readList);
        ArrayList<ListData> list = new ArrayList<>();
        ListData item = new ListData();
        item.setColum("Reading");
        ListData item2 = new ListData();
        item2.setColum("ReadOver");
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
        gallery.setInfo("这是一则画廊日志，你可以上传图片，并且为每一个图片添加信息。");
        LocalDateTime localDateTime = LocalDateTime.now();
        gallery.setCreateTime(localDateTime);
        String s = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        gallery.setId(user_id + "Gallery");
        View galleryTable = new View();
        galleryTable.setNoteId(gallery.getId());
        galleryTable.setPath("");
        galleryTable.setId(gallery.getId() + s);
        galleryTable.setInfo("");
        galleryTable.setFname(gallery.getName());
        galleryTable.setCreateTime(localDateTime);
        galleryTable.setIcon("iconfont el-icon-dian");
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
        String s = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        videos.setId(user_id + "Video");
        View videoList = new View();
        videoList.setId(videos.getId() + s);
        videoList.setNoteId(videos.getId());
        videoList.setType("view");
        videoList.setComponent("ListView");
        videoList.setFname(videos.getName());
        videoList.setInfo("");
        videoList.setIcon("iconfont el-icon-dian");
        videoList.setCreateTime(localDateTime);
        videoList.setName("videoList");
        videoList.setPath("");
        ListData item = new ListData();
        item.setColum("Films");
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
        String s = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        travelNote.setId(user_id + "Travel");
        View travelTable = new View();
        travelTable.setNoteId(travelNote.getId());
        travelTable.setId(travelNote.getId() + s);
        travelTable.setInfo("");
        travelTable.setPath("");
        travelTable.setCreateTime(localDateTime);
        travelTable.setName("TravelTable");
        travelTable.setType("view");
        travelTable.setFname(travelNote.getName());
        travelTable.setComponent("TableView");
        travelTable.setIcon("iconfont el-icon-dian");
        ArrayList<String> colums = new ArrayList<>();
        colums.add("name");
        colums.add("time");
        colums.add("place");
        colums.add("people");
        Etable etable = new Etable();
        etable.setViewId(travelTable.getId());
        etable.setColums(JSON.toJSONString(colums));
        int save = noteMapper.insert(travelNote);
        int i = viewMapper.insert(travelTable);
        int d = etableMapper.insert(etable);
        return (save == 1 && i == 1 && d == 1);
    }
}
