package com.example.meetings.auth.security.model;

import com.example.meetings.user.model.domain.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;

@Accessors(chain = true)
public class AuthUserDetails implements UserDetails, OAuth2User {

    @Getter
    @Setter
    private Long userId;

    @Setter
    private String password;

    @Getter
    @Setter
    private List<Role> roles = new ArrayList<>();

    @Setter
    private boolean accountNonExpired;

    @Setter
    private boolean accountNonLocked;

    @Setter
    private boolean credentialsNonExpired;

    @Setter
    private boolean enabled;

    public AuthUserDetails() {
        this.accountNonLocked = true;
        this.accountNonExpired = true;
        this.credentialsNonExpired = true;
        this.enabled = true;
    }

    @Override
    public String getName() {
        return valueOf(userId);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return valueOf(userId);
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
