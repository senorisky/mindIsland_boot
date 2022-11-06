package com.lifemind.bluer.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lifemind.bluer.entity.Code;
import com.lifemind.bluer.entity.Note;
import com.lifemind.bluer.entity.Result;
import com.lifemind.bluer.service.impl.NoteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ckz
 * @since 2022-11-06
 */
@RestController
@RequestMapping("/note")
public class NoteController {
    @Autowired
    private NoteServiceImpl noteService;

    @RequestMapping("/saveNote")//post
    public Result saveNote(@RequestBody Note note) {
        boolean save = noteService.saveOrUpdate(note);
        if (save) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("note", note);
            return new Result(hashMap, Code.SUCCESS, "修改成功");
        } else {
            return new Result(null, Code.SYSTEM_ERROR, "修改失败");
        }
    }

    @RequestMapping("/addNote")//post
    public Result addNote(@RequestBody Note note) {
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

    @RequestMapping("/giveNoteIcon")
    public Result giveNoteIcon(@RequestParam String path, @RequestParam String note_id) {
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("note_id", note_id);
        updateWrapper.set("note_icon", path);
        boolean update = noteService.update(null, updateWrapper);
        if (update) {
            return new Result(null, Code.SUCCESS, "");
        }
        return new Result(null, Code.SYSTEM_ERROR, "");

    }

    @DeleteMapping("/deleteNote")
    public Result deleteNote(@RequestParam String note_id, @RequestParam String user_id) {
        boolean save = noteService.removeById(note_id);
        if (save) {
            QueryWrapper wrapper = new QueryWrapper();
            wrapper.eq("user_id", user_id);
            List<Note> notes = noteService.list(wrapper);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("notes", notes);
            return new Result(hashMap, Code.SUCCESS, "删除成功");
        } else {
            return new Result(null, Code.SYSTEM_ERROR, "删除失败");
        }
    }

    @RequestMapping("/getAll")
    public Result getAllNotes(@RequestParam String user_id) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("user_id", user_id);
        List<Note> notes = noteService.list(wrapper);
        if (notes != null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("notes", notes);
            return new Result(hashMap, Code.SUCCESS, "读取成功");
        } else {
            return new Result(null, Code.SYSTEM_ERROR, "读取失败");
        }

    }
}
