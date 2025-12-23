package com.example.meetings.common.utils;

import com.example.meetings.user.model.domain.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;

@Service
public class JWTUtil {
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    @Value("${jwt.sessionTime}")
    private long sessionTime;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        String commaSeparatedListOfAuthorities = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(joining(","));
        claims.put("authorities", commaSeparatedListOfAuthorities);

        return createToken(claims, user.getUserId());
    }

    public Long extractUserId(String token) {
        return Long.parseLong(extractClaim(token, Claims::getSubject));
    }

    public String extractAuthorities(String token) {
        return extractClaim(token, claims -> (String) claims.get("authorities"));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private String createToken(Map<String, Object> claims, Long subject) {

        return Jwts.builder().setClaims(claims)
                .setSubject(subject.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(/*expireTimeFromNow()*/null)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    private Date expireTimeFromNow() {
        return new Date(System.currentTimeMillis() + sessionTime);
    }
}
