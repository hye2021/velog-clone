package org.example.blog.security.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// CustomeUserDetails
// Spring Security에서 사용자의 정보를 담는 클래스
// UserDetails 인터페이스를 구현하여 사용자의 정보를 담는다.
public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final String name;
    private final List<GrantedAuthority> authorities; // 사용자의 권한

    public CustomUserDetails(String username, String password, String name, List<String> roles) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
