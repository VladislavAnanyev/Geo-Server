package com.example.meetings.moderation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "text.moderation.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpTextModerationService implements TextModerationService {

    @Override
    public void validateUserProfileText(String fieldLabel, String text) {
    }

    @Override
    public void validatePlanText(String title, String description) {
    }
}
