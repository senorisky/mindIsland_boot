package com.lifemind.bluer.controller;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lifemind.bluer.entity.*;
import com.lifemind.bluer.entity.Dto.MenuData;
import com.lifemind.bluer.service.impl.UserServiceImpl;
import com.lifemind.bluer.uitls.CheckCodeUtil;
import com.lifemind.bluer.uitls.TokenUtil;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender jms;

    @Value("${spring.mail.username}")
    private String from;

    @Transactional
    @PostMapping("/register")
    @ResponseBody
    public Result userRegist(@RequestBody HashMap<String, Object> data, HttpSession session) {
        String userStr = JSON.toJSONString(data.get("user"));
        User user = JSON.parseObject(userStr, User.class);
        String email = user.getEmail();
        String password = user.getPassword();
        String checkCode = (String) (data.get("CheckCode"));
        String checkCode1 = (String) session.getAttribute(user.getEmail());
        System.out.println("用户注册\n" + user + "\n" + checkCode1 + checkCode);
        if (checkCode1 == null) {
            return new Result(null, Code.SYSTEM_ERROR, "验证码已过期");
        }
        if (!checkCode1.equals(checkCode)) {
            return new Result(null, Code.SYSTEM_ERROR, "验证码错误");
        }
        //判断是否为空
        if (StrUtil.isBlank(email) || StrUtil.isBlank(password))
            return new Result(null, Code.OTHER_EVENT_ERROR, "邮箱或密码不能为空");
        try {
            if (!userService.checkExist(email)) {
                if (userService.regist(user)) {
                    System.out.println("注册成功");
                    return new Result(null, Code.SUCCESS, "注册成功,点击左侧进行登录");
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

    @RequestMapping("/emailCheck")
    public Result getCheckCode(@RequestParam(value = "email") String email, @RequestParam(defaultValue = "r") String type, HttpSession session) {
        MimeMessage message = null;
        try {
            message = jms.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            String s = CheckCodeUtil.generateVerifyCode(6);
            helper.setTo(email); // 接收地址
            helper.setSubject("您正在使用邮箱注册LifeMind，若非本人操作请忽略此邮件"); // 标题
            if ("r".equals(type))
                helper.setSubject("您正在使用邮箱注册LifeMind，若非本人操作请忽略此邮件"); // 标题
            else if ("rs".equals(type)) {
                helper.setSubject("您正在重置LifeMind密码，若非本人操作请忽略此邮件"); // 标题
            } else {
                return new Result(null, Code.SYSTEM_ERROR, "发送失败");
            }
            Context context = new Context();
            context.setVariable("code", s);
            session.setAttribute(email, s);
            session.setMaxInactiveInterval(300);
            String template = templateEngine.process("emailTmp", context);
            helper.setText(template, true);
            jms.send(message);
            return new Result(null, Code.SUCCESS, "发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(null, Code.SYSTEM_ERROR, "发送失败");
        }
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
                System.out.println("登录成功" + token);
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

    @RequestMapping("/logout")
    @ResponseBody
    public Result Logout(@RequestParam String userId,
                         @RequestHeader(value = "lm-token") String token) {

        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("user_id", userId);
        User check;
        try {
            check = userService.getOne(wrapper);

            check.setPassword(null);
            check.setCreateTime(null);
            check.setLoginTime(null);
            boolean b = TokenUtil.Destroy(token);
            if (b)
                return new Result(null, Code.SUCCESS, "登出成功");
            else
                return new Result(null, Code.SYSTEM_ERROR, "登出成失败");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(null, Code.SYSTEM_ERROR, "系统错误");
        }
    }

    @Transactional
    @RequestMapping("/delete")
    @ResponseBody
    public Result Delete(@RequestParam String userId,
                         @RequestParam String passwd,
                         @RequestHeader(value = "lm-token") String token) {

        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }

        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("user_id", userId);
        wrapper.eq("password", passwd);
        User check;
        try {
            check = userService.getOne(wrapper);
            if (check == null) {
                return new Result(null, Code.LOGIN_ERROR, "密码错误");
            }
            check.setPassword(null);
            check.setCreateTime(null);
            check.setLoginTime(null);
            boolean b = TokenUtil.Destroy(token);
            boolean d = userService.deleteUser(userId);
            if (b && d) {
                //删除资源
                File home = new File("D:/LifeMind/" + userId);
                if (home.exists()) {
                    FileUtils.deleteDirectory(home);
                }
                return new Result(null, Code.SUCCESS, "删除账户成功");
            } else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new Result(null, Code.SYSTEM_ERROR, "删除账户失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(null, Code.SYSTEM_ERROR, "系统错误");
        }
    }

    @RequestMapping("/save")
    @ResponseBody
    public Result saveUser(@RequestParam(value = "name") String name,
                           @RequestParam(value = "email") String email,
                           @RequestParam(value = "des") String des,
                           @RequestParam(value = "oldpass") String oldpass,
                           @RequestParam(value = "newpass") String newpass,
                           @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        User user = TokenUtil.getUser(token);
        boolean updatePasswd = false;
        if (!"".equals(newpass) && newpass != null) {
            User user1 = TokenUtil.getUser(token);
            QueryWrapper wrapper = new QueryWrapper();
            wrapper.eq("user_id", user1.getUserId());
            User one = userService.getOne(wrapper);
            if (oldpass.equals(one.getPassword())) {
                one.setPassword(newpass);
                updatePasswd = userService.update(one, wrapper);
                if (!updatePasswd) {
                    return new Result(null, Code.SYSTEM_ERROR, "系统错误");
                }
            } else
                return new Result(null, Code.PASS_WORD_WRONG, "原密码输入错误");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("user_id", user.getUserId());
        User one = userService.getOne(wrapper);
        if (!"".equals(email) && email != null)
            one.setEmail(email);
        if (!"".equals(name) && name != null)
            one.setUserName(name);
        if (!"".equals(des) && des != null)
            one.setDes(des);
        boolean b = userService.update(one, wrapper);
        if (b) {
            one.setPassword(null);
            TokenUtil.changeUser(token, one);
            HashMap<String, Object> data = new HashMap<>();
            data.put("user", one);
            if (updatePasswd)
                data.put("passnew", true);
            return new Result(data, Code.SUCCESS, "个人信息更新成功");

        }
        return new Result(null, Code.SYSTEM_ERROR, "个人信息更新失败");
    }

    @RequestMapping("/getuser")
    @ResponseBody
    public Result getUser(@RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        User user = TokenUtil.getUser(token);
        HashMap<String, Object> data = new HashMap<>();
        data.put("user", user);
        return new Result(data, Code.SUCCESS, "获取成功");
    }

    @RequestMapping("/header")
    @ResponseBody

    public Result UpLoadPics(@RequestParam String userId,
                             @RequestParam MultipartFile file,
                             @RequestHeader(value = "lm-token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        File pichome = new File("/LifeMind/" + userId);
        if (!pichome.exists()) {
            pichome.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        String filePath = pichome.getAbsolutePath() + "/" + fileName;
        try {
            System.out.println(filePath);
            //将文件保存指定目录
            File newpic = new File(filePath);
            if (newpic.exists()) {
                newpic.deleteOnExit();
            }
            file.transferTo(newpic);
            String fileUrl = "http://localhost:8081/LifeMind/" + userId + "/" + fileName;
            //存入数据库，
            QueryWrapper wrapper = new QueryWrapper();
            wrapper.eq("user_id", userId);
            User one = TokenUtil.getUser(token);
            one.setPassword(null);
            //删除之前的头像
            String lastUrl = one.getAvatar();
            if (lastUrl != null) {
                String lastPath = "D:/" + lastUrl.substring(lastUrl.indexOf("LifeMind"));
                File lastHeader = new File(lastPath);
                lastHeader.deleteOnExit();
            }
            one.setAvatar(fileUrl);
            boolean update = userService.update(one, wrapper);
            if (update) {
                HashMap<String, Object> data = new HashMap<>();
                data.put("user", one);
                return new Result(data, Code.SUCCESS, "更换头像成功");
            }
            return new Result(null, Code.SYSTEM_ERROR, "上传失败,可能存在同名图片,请修改");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(null, Code.SYSTEM_ERROR, "上传失败,可能存在同名图片,请修改");
        }
    }

    @RequestMapping("/forget")
    @ResponseBody
    public Result resetPw(@RequestParam String checkCode,
                          HttpSession session,
                          @RequestParam String email, @RequestParam String password) {

        String checkCode1 = (String) session.getAttribute(email);
        if (checkCode1 == null) {
            return new Result(null, Code.SUCCESS, "验证码已过期");
        }
        if (!checkCode1.equals(checkCode)) {
            return new Result(null, Code.SUCCESS, "验证码错误");
        }
        UpdateWrapper wrapper = new UpdateWrapper();
        wrapper.eq("email", email);
        wrapper.set("password", password);
        boolean update = userService.update(wrapper);
        if (update) {
            return new Result(null, Code.SUCCESS, "修改成功");
        }
        return new Result(null, Code.SUCCESS, "修改失败");
    }

}
