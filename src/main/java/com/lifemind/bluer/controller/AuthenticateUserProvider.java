package com.lifemind.bluer.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lifemind.bluer.entity.User;
import com.lifemind.bluer.service.IUserService;
import com.lifemind.bluer.service.impl.IuserDetailsServiceImpl;
import com.lifemind.bluer.uitls.MySecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @Classname AuthenticateUserProvider
 * @Description TODO
 * @Date 2023/2/25 10:17
 * @Created by senorisky
 */
@Component
public class AuthenticateUserProvider implements AuthenticationProvider {

    @Autowired
    private IuserDetailsServiceImpl siteUserDetailsService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws org.springframework.security.core.AuthenticationException {
        //自定义的装载用户信息的类
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
        String s = MySecurityUtil.desEncrypt(password);
        UserDetails userDetails = siteUserDetailsService.loadUserByUsername(username);
        if (passwordEncoder.matches(s, userDetails.getPassword())) {
            return new UsernamePasswordAuthenticationToken(username, null, null);
        } else {
            throw new BadCredentialsException("用户名密码不正确，请重新登陆！");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (Authentication.class.isAssignableFrom(authentication));
    }
}
