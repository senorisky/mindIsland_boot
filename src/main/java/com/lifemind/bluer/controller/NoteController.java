package com.lifemind.bluer.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lifemind.bluer.entity.Code;
import com.lifemind.bluer.entity.Note;
import com.lifemind.bluer.entity.Result;
import com.lifemind.bluer.entity.User;
import com.lifemind.bluer.service.INoteService;
import com.lifemind.bluer.service.impl.NoteServiceImpl;
import com.lifemind.bluer.uitls.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 * Note  Page
 *
 * @author ckz
 * @since 2022-11-06
 */
@RestController
@RequestMapping("/note")
public class NoteController {
    @Autowired
    private INoteService noteService;

    @RequestMapping("/saveNote")//post
    public Result saveNote(@RequestBody Note note, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        boolean save = noteService.saveOrUpdate(note);
        if (save) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("note", note);
            return new Result(hashMap, Code.SUCCESS, "修改成功");
        } else {
            return new Result(null, Code.SYSTEM_ERROR, "修改失败");
        }
    }

    @RequestMapping("/saveNoteInfo")
    @ResponseBody
    public Result changePic(
            @RequestParam(value = "noteId") String nid,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "profile") String profile,
            @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("id", nid);
        Note one = noteService.getOne(wrapper);
        one.setName(name);
        one.setInfo(profile);
        boolean update = noteService.update(one, wrapper);
        if (update) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("note", one);
            return new Result(hashMap, Code.SUCCESS, "保存成功");
        } else {
            return new Result(null, Code.SYSTEM_ERROR, "保存失败");
        }
    }

    @RequestMapping("/addNote")//post
    public Result addNote(@RequestBody Note note, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        boolean save = noteService.save(note);
        if (save) {
            QueryWrapper wrapper = new QueryWrapper();
            wrapper.eq("user_id", note.getUserId());
            List<Note> notes = noteService.list(wrapper);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("notes", notes);
            return new Result(hashMap, Code.SUCCESS, "添加成功");
        } else {
            return new Result(null, Code.SYSTEM_ERROR, "添加失败");
        }
    }

    @Transactional
    @RequestMapping("/addPage")//post
    public Result addPage(@RequestBody Note note, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        boolean save = noteService.save(note);
        if (save) {
            boolean b = noteService.InitDefaultPage(note.getId());
            if (b) {
                QueryWrapper wrapper = new QueryWrapper();
                wrapper.eq("user_id", note.getUserId());
                List<Note> notes = noteService.list(wrapper);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("notes", notes);
                return new Result(hashMap, Code.SUCCESS, "添加成功");
            } else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new Result(null, Code.SYSTEM_ERROR, "添加失败");

            }
        } else {
            return new Result(null, Code.SYSTEM_ERROR, "添加失败");
        }
    }

    @RequestMapping("/giveNoteIcon")
    public Result giveNoteIcon(@RequestParam String path, @RequestParam String note_id, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("note_id", note_id);
        updateWrapper.set("note_icon", path);
        boolean update = noteService.update(null, updateWrapper);
        if (update) {
            return new Result(null, Code.SUCCESS, "");
        }
        return new Result(null, Code.SYSTEM_ERROR, "");
    }

    @Transactional
    @RequestMapping("/deleteNote")
    public Result deleteNote(@RequestParam(value = "noteId") String note_id,
                             @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        User user = TokenUtil.getUser(token);
        try {
            boolean save = noteService.removeNote(note_id, user.getUserId());
            if (save) {
                QueryWrapper wrapper = new QueryWrapper();
                wrapper.eq("user_id", user.getUserId());
                wrapper.orderByAsc("create_time");
                List<Note> notes = noteService.getMenuData(user.getUserId());
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("notes", notes);
                return new Result(hashMap, Code.SUCCESS, "删除成功");
            } else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new Result(null, Code.SYSTEM_ERROR, "删除失败");
            }
        } catch (IOException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Result(null, Code.SYSTEM_ERROR, "删除失败");
        }
    }

    @RequestMapping("/getAll")
    public Result getAllNotes(@RequestParam String user_id, @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("user_id", user_id);
        wrapper.orderByAsc("create_time");
        List<Note> notes = noteService.getMenuData(user_id);
        System.out.println(notes);
        if (notes != null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("notes", notes);
            return new Result(hashMap, Code.SUCCESS, "读取成功");
        } else {
            return new Result(null, Code.SYSTEM_ERROR, "读取失败");
        }

    }
}
