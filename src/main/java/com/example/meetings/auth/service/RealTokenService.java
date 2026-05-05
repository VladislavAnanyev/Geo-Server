package com.example.meetings.auth.service;

import com.example.meetings.auth.model.UserToken;
import com.example.meetings.auth.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import static java.util.UUID.randomUUID;

@Service
@ConditionalOnProperty(value = "redis.impl", havingValue = "real")
public class RealTokenService implements TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    /**
     * Сгенерировать токен обновления для пользователя
     *
     * @param userId идентификатор пользователя
     * @return сгенерированный токен
     */
    @Override
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
