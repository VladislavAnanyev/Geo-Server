package com.example.meetings.auth.service;

import com.example.meetings.auth.model.UserToken;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.UUID.randomUUID;

@Service
@ConditionalOnProperty(value = "redis.impl", havingValue = "mock")
public class MockTokenService implements TokenService {

    private final Map<String, UserToken> tokens = new ConcurrentHashMap<>();

    @Override
    public String createToken(Long userId) {
        String refreshToken = randomUUID().toString();
        tokens.put(refreshToken, new UserToken()
                .setRefreshToken(refreshToken)
                .setUserId(userId));
        return refreshToken;
    }

    @Override
    public UserToken updateRefreshToken(String oldRefreshToken) {
        UserToken userToken = tokens.get(oldRefreshToken);
        if (userToken == null) {
            throw new EntityNotFoundException("exception.token.not.found");
        }

        tokens.remove(oldRefreshToken);
        String newRefreshToken = randomUUID().toString();
        userToken.setRefreshToken(newRefreshToken);
        tokens.put(newRefreshToken, userToken);
        return userToken;
    }
}
