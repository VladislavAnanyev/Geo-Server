package com.example.mywebquizengine.service.utils;

import com.example.mywebquizengine.model.userinfo.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public class AuthenticationUtil {
    public static void setAuthentication(User user, List<GrantedAuthority> authorityList) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        user, null, authorityList); // Проверить работу getAuthorities
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }
}
