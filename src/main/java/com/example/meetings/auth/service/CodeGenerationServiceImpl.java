package com.example.meetings.auth.service;

import com.example.meetings.common.utils.CodeUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Реализация генерации коротких одноразовых кодов при включённой отправке SMS.
 */
@Service
@ConditionalOnProperty(value = "codesender.enabled", havingValue = "true")
public class CodeGenerationServiceImpl implements CodeGenerationService {

    /**
     * Генерирует короткий числовой код через {@link CodeUtil}.
     *
     * @return сгенерированный код
     */
    @Override
    public String generate() {
        return CodeUtil.generateShortCode();
    }
}
