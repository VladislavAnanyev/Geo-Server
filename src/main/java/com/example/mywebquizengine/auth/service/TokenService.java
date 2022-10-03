package com.example.mywebquizengine.auth.service;

import com.example.mywebquizengine.auth.repository.TokenRepository;
import com.example.mywebquizengine.auth.model.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    public String createToken(Long userId) {
        UserToken userToken = new UserToken();
        String refreshToken = UUID.randomUUID().toString();
        userToken.setRefreshToken(refreshToken);
        userToken.setUserId(userId);
        tokenRepository.save(userToken);
        return refreshToken;
    }

}
