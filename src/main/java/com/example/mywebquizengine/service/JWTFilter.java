package com.example.mywebquizengine.service;

import com.example.mywebquizengine.security.ActiveUserStore;
import com.example.mywebquizengine.security.LoggedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Component
public class JWTFilter extends OncePerRequestFilter {
    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private ActiveUserStore activeUserStore;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            //если подпись не совпадает с вычисленной, то SignatureException
            //если подпись некорректная (не парсится), то MalformedJwtException
            //если время подписи истекло, то ExpiredJwtException
            username = jwtUtil.extractUsername(jwt);
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String commaSeparatedListOfAuthorities = jwtUtil.extractAuthorities(jwt);
            List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(commaSeparatedListOfAuthorities);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            username, "Bearer", authorities);
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            HttpSession session = request.getSession(false);
            if (session != null) {

                LoggedUser user = new LoggedUser(SecurityContextHolder.getContext().getAuthentication().getName(), activeUserStore);
                session.invalidate();
                session = request.getSession(true);
                session.setAttribute("user", user);
                session.setMaxInactiveInterval(60);
                session.setAttribute("abc", "AAA");
            } else {
                LoggedUser user = new LoggedUser(SecurityContextHolder.getContext().getAuthentication().getName(), activeUserStore);

                session = request.getSession(true);
                session.setAttribute("user", user);
                session.setMaxInactiveInterval(60);
                session.setAttribute("abc", "AAA");
            }

        }
        chain.doFilter(request, response);
    }
}
