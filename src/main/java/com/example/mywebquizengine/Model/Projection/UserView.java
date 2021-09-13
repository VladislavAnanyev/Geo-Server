package com.example.mywebquizengine.Model.Projection;

import com.example.mywebquizengine.Model.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public interface UserView {
    String getUsername();
    String getFirstName();
    String getLastName();
    String getAvatar();
    Integer getBalance();
    List<Role> getRoles();
    List<GrantedAuthority> getAuthorities();
    boolean isStatus();
    String getActivationCode();
    String getChangePasswordCode();
}
