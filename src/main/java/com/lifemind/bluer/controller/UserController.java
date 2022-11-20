package com.lifemind.bluer.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lifemind.bluer.entity.Code;
import com.lifemind.bluer.entity.Dto.MenuData;
import com.lifemind.bluer.entity.Note;
import com.lifemind.bluer.entity.Result;
import com.lifemind.bluer.entity.User;
import com.lifemind.bluer.service.impl.UserServiceImpl;
import com.lifemind.bluer.uitls.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ckz
 * @since 2022-11-06
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @Transactional
    @PostMapping("/register")
    @ResponseBody
    public Result userRegist(@RequestBody User user) {
        System.out.println("用户注册");
        String email = user.getEmail();
        String password = user.getPassword();
        //判断是否为空
        if (StrUtil.isBlank(email) || StrUtil.isBlank(password))
            return new Result(null, Code.OTHER_EVENT_ERROR, "邮箱或密码不能为空");
        try {
            if (!userService.checkExist(email)) {
                if (userService.regist(user)) {
                    System.out.println("注册成功");
                    return new Result(null, Code.SUCCESS, "注册成功");
                } else {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                }
            } else {
                return new Result(null, Code.USER_EXIST, "用户已存在");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(null, Code.SYSTEM_ERROR, "系统错误");
    }

    @RequestMapping("/login")
    @ResponseBody
    public Result Login(@RequestBody User user) {
        System.out.println(user);
        String email = user.getEmail();
        String password = user.getPassword();
        //判断是否为空
        if (StrUtil.isBlank(email) || StrUtil.isBlank(password))
            return new Result(null, Code.OTHER_EVENT_ERROR, "邮箱或密码不能为空");
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("email", user.getEmail());
        User check;
        try {
            check = userService.getOne(wrapper);
            if (check == null) {
                return new Result(null, Code.USER_EXIST, "用户不存在，请注册");
            } else if (password.equals(check.getPassword())) {
                check.setLoginTime(LocalDateTime.now());
                userService.updateById(check);
                HashMap<String, Object> data = new HashMap<String, Object>();
                check.setPassword(null);
                check.setCreateTime(null);
                check.setLoginTime(null);
                String token = TokenUtil.generateToken(check);
                data.put("user", check);
                data.put("token", token);
                System.out.println("登录成功");
                List<Note> menuData = userService.getMenuData(check.getUserId());
                data.put("menuData", menuData);
                return new Result(data, Code.SUCCESS, "登录成功");
            } else
                return new Result(null, Code.LOGIN_ERROR, "密码错误");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(null, Code.SYSTEM_ERROR, "系统错误");
        }
    }

    @RequestMapping("/saveUser")
    @ResponseBody
    public Result saveUser(@RequestBody User user) {
        boolean b = userService.saveOrUpdate(user);
        if (b) {
            return new Result(null, Code.SUCCESS, "");
        }
        return new Result(null, Code.SYSTEM_ERROR, "");
    }

}
