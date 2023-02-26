package com.lifemind.bluer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

/**
 * @Classname AuthUser
 * @Description TODO
 * @Date 2023/2/24 22:37
 * @Created by senorisky
 */
@Data
@AllArgsConstructor  //全参构造
@NoArgsConstructor  //无参构造
public class AuthUser implements UserDetails {
    private User user;
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    // 账户是否未被锁
    @Override
    public boolean isAccountNonLocked() {
        return false;
    }
    // 账户是否未过期

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
