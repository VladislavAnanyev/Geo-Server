package com.example.meetings.auth.service;

import com.example.meetings.auth.model.UserToken;
import com.example.meetings.auth.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.UUID.randomUUID;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    /**
     * Сгенерировать токен обновления для пользователя
     *
     * @param userId идентификатор пользователя
     * @return сгенерированный токен
     */
    public String createToken(Long userId) {
        String refreshToken = randomUUID().toString();
        tokenRepository.save(
                new UserToken()
                        .setRefreshToken(refreshToken)
                        .setUserId(userId)
        );

        return refreshToken;
    }

}
