package com.example.mywebquizengine.auth.repository;

import com.example.mywebquizengine.auth.model.UserToken;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

public interface TokenRepository extends KeyValueRepository<UserToken, String> {
}
