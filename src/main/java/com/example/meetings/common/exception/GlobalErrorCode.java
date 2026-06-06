package com.example.meetings.common.exception;

public class GlobalErrorCode {
    public static final Long ERROR_WRONG_USERNAME_OR_PASSWORD = 1000L;
    public static final Long ERROR_USER_ALREADY_REGISTERED = 1001L;
    public static final Long ERROR_WRONG_CHANGE_PASSWORD_CODE = 1002L;
    public static final Long ERROR_USER_NOT_FOUND = 1003L;
    public static final Long ERROR_CODE_EXPIRED = 1004L;
    public static final Long UNAUTHORIZED = 1005L;

    public static final Long ERROR_SUBSCRIPTION_FRIEND_REQUESTS_DAILY_LIMIT = 1100L;
    public static final Long ERROR_SUBSCRIPTION_MEETINGS_HISTORY_LIMIT = 1101L;
    public static final Long ERROR_SUBSCRIPTION_SCHEDULED_PLANS_ACTIVE_LIMIT = 1102L;
    public static final Long ERROR_SUBSCRIPTION_LOCATION_PEOPLE = 1103L;
}
