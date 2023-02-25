package com.lifemind.bluer.service.impl;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lifemind.bluer.entity.AuthUser;
import com.lifemind.bluer.entity.User;
import com.lifemind.bluer.mapper.UserMapper;
import com.lifemind.bluer.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Classname UserAuthImpl
 * @Description TODO
 * @Date 2023/2/24 22:36
 * @Created by senorisky
 */
@Service(value = "userDetailsService")
public class IuserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 通过账号查找用户、角色的信息
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email", username);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            //用户名不存在
            throw new UsernameNotFoundException("user is not exist");
        } else {
            //实际应该查询权限，但是目前没有设置权限问题 登录即可
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            System.out.println("用户登录认证完毕");
            return new AuthUser(user, authorities);
        }
    }

//    /**
//     * 构建userdetails
//     *
//     * @param user 用户信息
//     * @return
//     */
//    private UserDetails getUserDetails(User user) {
//
//        Set<String> dbAuthsSet = new HashSet<>();
//        if (ArrayUtil.isNotEmpty(user.getRoles())) {
//            // 获取角色
//            Arrays.stream(user.getRoles()).forEach(roleId -> dbAuthsSet.add(ROLE + roleId));
//            // 获取资源
//            dbAuthsSet.addAll(Arrays.asList(user.getPermissions()));
//        }
//        Collection<? extends GrantedAuthority> authorities = AuthorityUtils
//                .createAuthorityList(dbAuthsSet.toArray(new String[0]));
//        // 构造security用户
//        AuthUser authUser = new AuthUser(user, authorities);
//        return authUser;
//    }
}

