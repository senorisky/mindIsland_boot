package com.lifemind.bluer.controller;


import com.lifemind.bluer.entity.Code;
import com.lifemind.bluer.entity.Elist;
import com.lifemind.bluer.entity.Result;
import com.lifemind.bluer.service.impl.ElistServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ckz
 * @since 2022-11-06
 */
@RestController
@RequestMapping("/elist")
public class ElistController {
    @Autowired
    private ElistServiceImpl elistService;

    @DeleteMapping("/deleteNoteList")
    @ResponseBody
    public Result deleteNoteList(@RequestBody String list_id) {
        boolean b = elistService.removeById(list_id);
        if (b) {
            return new Result(null, Code.SUCCESS, "");
        }
        return new Result(null, Code.SYSTEM_ERROR, "");
    }

    @RequestMapping("/addList")
    @ResponseBody
    public Result addNoteList(@RequestBody Elist list) {
        boolean b = elistService.save(list);
        if (b) {
            return new Result(null, Code.SUCCESS, "");
        }
        return new Result(null, Code.SYSTEM_ERROR, "");
    }
}
