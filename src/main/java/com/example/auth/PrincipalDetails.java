package com.example.auth;

import com.example.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class PrincipalDetails implements UserDetails, OAuth2User {
    private User user;

    public PrincipalDetails(User user) {
        this.user = user;
    }


    //  권한 관력 작업을 하기 위한 role return
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collections = new ArrayList<>();
        collections.add(() -> {
            return user.getRole().name();
        });
        return collections;
    }

    //  getPassword 메서드
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    //  getUsername 메서드
    @Override
    public String getUsername() {
        return user.getLoginId();
    }

    //  계정이 만료되었는지 (true: 만료X)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //  계정이 잠겼는지 (true: 잠기지 않음)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //  비밀번호가 만료되었는지 (true: 만료X)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 활성화 상태인지 (true: 활성화)
    @Override
    public boolean isEnabled() {
        return true;
    }


    //  OAuth 로그
    private Map<String, Object> attributes;

    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

}
