package com.example.meetings.moderation;

import com.example.meetings.common.exception.LogicException;
import com.example.meetings.integration.moderation.ModerationLlmClient;
import com.example.meetings.integration.ollama.OllamaModerationResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import static com.example.meetings.util.Constants.EXCEPTION_TEXT_MODERATION_REJECTED;
import static com.example.meetings.util.Constants.EXCEPTION_TEXT_MODERATION_UNAVAILABLE;
import static com.example.meetings.util.Constants.JSON_OBJECT_END;
import static com.example.meetings.util.Constants.JSON_OBJECT_START;
import static com.example.meetings.util.Constants.MODERATION_FIELD_FIRST_NAME;
import static com.example.meetings.util.Constants.MODERATION_FIELD_PLAN;
import static com.example.meetings.util.Constants.MODERATION_PLAN_TEXT_SEPARATOR;
import static com.example.meetings.util.Constants.MODERATION_PROMPT_DESCRIPTION;
import static com.example.meetings.util.Constants.MODERATION_PROMPT_FIRST_NAME;
import static com.example.meetings.util.Constants.MODERATION_PROMPT_PLAN;
import static com.example.meetings.util.Constants.PROMPT_QUOTE_LEFT;
import static com.example.meetings.util.Constants.PROMPT_QUOTE_REPLACEMENT;
import static com.example.meetings.util.Constants.PROMPT_QUOTE_RIGHT;

@Service
@ConditionalOnProperty(name = "text.moderation.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class LlmTextModerationService implements TextModerationService {

    private final ModerationLlmClient moderationLlmClient;
    private final ObjectMapper objectMapper;

    @Value("${text.moderation.max-text-length:1000}")
    private int maxTextLength;

    @Override
    public void validateUserProfileText(String fieldLabel, String text) {
        validateText(fieldLabel, text);
    }

    @Override
    public void validatePlanText(String title, String description) {
        validateText(MODERATION_FIELD_PLAN, joinPlanText(title, description));
    }

    private void validateText(String fieldLabel, String text) {
        if (StringUtils.isBlank(text)) {
            return;
        }
        String trimmed = truncate(StringUtils.trim(text));
        if (!isAllowed(trimmed, fieldLabel)) {
            throw new LogicException(EXCEPTION_TEXT_MODERATION_REJECTED);
        }
    }

    private static String joinPlanText(String title, String description) {
        String trimmedTitle = StringUtils.trimToEmpty(title);
        String trimmedDescription = StringUtils.trimToEmpty(description);
        if (StringUtils.isBlank(trimmedDescription)) {
            return trimmedTitle;
        }
        if (StringUtils.isBlank(trimmedTitle)) {
            return trimmedDescription;
        }
        return trimmedTitle + MODERATION_PLAN_TEXT_SEPARATOR + trimmedDescription;
    }

    private boolean isAllowed(String text, String fieldLabel) {
        String prompt = buildPrompt(fieldLabel, escapeForPrompt(text));
        try {
            String rawResponse = moderationLlmClient.generate(prompt);
            if (StringUtils.isBlank(rawResponse)) {
                log.warn("LLM moderation empty response for field {}", fieldLabel);
                throw new LogicException(EXCEPTION_TEXT_MODERATION_UNAVAILABLE);
            }
            return parseAllowed(rawResponse);
        } catch (RestClientException e) {
            log.warn("LLM moderation failed for field {}: {}", fieldLabel, e.getMessage());
            throw new LogicException(EXCEPTION_TEXT_MODERATION_UNAVAILABLE);
        }
    }

    private boolean parseAllowed(String raw) {
        String json = extractJsonObject(raw);
        try {
            OllamaModerationResult moderationResult = objectMapper.readValue(json, OllamaModerationResult.class);
            return moderationResult.isResult();
        } catch (JsonProcessingException e) {
            log.warn("LLM moderation invalid JSON: {}", raw);
            throw new LogicException(EXCEPTION_TEXT_MODERATION_UNAVAILABLE);
        }
    }

    private String extractJsonObject(String raw) {
        return substringJson(StringUtils.trim(raw));
    }

    private String substringJson(String trimmed) {
        int start = StringUtils.indexOf(trimmed, JSON_OBJECT_START);
        int end = StringUtils.lastIndexOf(trimmed, JSON_OBJECT_END);
        if (start >= 0 && end > start) {
            return StringUtils.substring(trimmed, start, end + 1);
        }
        if (isGuardrailsRefusal(trimmed)) {
            log.debug("LLM moderation guardrails refusal treated as rejected");
            return "{\"result\":false}";
        }
        log.warn("LLM moderation JSON not found in: {}", trimmed);
        throw new LogicException(EXCEPTION_TEXT_MODERATION_UNAVAILABLE);
    }

    private boolean isGuardrailsRefusal(String text) {
        return StringUtils.containsIgnoreCase(text, "не могу обсуждать эту тему");
    }

    private String truncate(String text) {
        if (StringUtils.length(text) <= maxTextLength) {
            return text;
        }
        return StringUtils.substring(text, 0, maxTextLength);
    }

    private String buildPrompt(String fieldLabel, String text) {
        if (MODERATION_FIELD_FIRST_NAME.equals(fieldLabel)) {
            return MODERATION_PROMPT_FIRST_NAME.formatted(text);
        }
        if (MODERATION_FIELD_PLAN.equals(fieldLabel)) {
            return MODERATION_PROMPT_PLAN.formatted(text);
        }
        return MODERATION_PROMPT_DESCRIPTION.formatted(text);
    }

    private String escapeForPrompt(String text) {
        return StringUtils.replaceEach(text,
                new String[]{PROMPT_QUOTE_LEFT, PROMPT_QUOTE_RIGHT},
                new String[]{PROMPT_QUOTE_REPLACEMENT, PROMPT_QUOTE_REPLACEMENT});
    }
}
