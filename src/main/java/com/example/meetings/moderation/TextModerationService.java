package com.example.meetings.moderation;

public interface TextModerationService {

    void validateUserProfileText(String fieldLabel, String text);

    void validatePlanText(String title, String description);
}
