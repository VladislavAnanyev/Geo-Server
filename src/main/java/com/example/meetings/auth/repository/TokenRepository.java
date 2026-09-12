package com.example.meetings.auth.repository;

import com.example.meetings.auth.model.UserToken;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

public interface TokenRepository extends KeyValueRepository<UserToken, String> {
}
