package com.example.mywebquizengine.common.utils;

import com.example.mywebquizengine.auth.security.model.AuthUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public class AuthenticationUtil {
    public static void setAuthentication(AuthUserDetails user, List<GrantedAuthority> authorityList) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, authorityList)
        );
    }
}
