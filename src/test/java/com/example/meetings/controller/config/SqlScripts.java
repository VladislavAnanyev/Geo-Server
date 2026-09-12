package com.example.meetings.controller.config;

/**
 * Shared SQL script paths for {@link org.springframework.test.context.jdbc.Sql}.
 */
public final class SqlScripts {

    public static final String CLEANUP = "/sql/cleanup.sql";
    public static final String USERS = "/sql/common/users.sql";
    public static final String FRIENDS = "/sql/common/friends.sql";
    public static final String GEOLOCATIONS = "/sql/common/geolocations.sql";
    public static final String MEETINGS = "/sql/common/meetings.sql";
    public static final String REQUESTS = "/sql/common/requests.sql";
    public static final String DIALOGS = "/sql/common/dialogs.sql";
    public static final String MESSAGES_1196 = "/sql/common/messages-1196.sql";
    public static final String MESSAGES_GROUP_1202 = "/sql/common/messages-group-1202.sql";
    public static final String MESSAGES_1224 = "/sql/common/messages-1224.sql";
    public static final String MESSAGES_1277 = "/sql/common/messages-1277.sql";
    public static final String DIALOG_UPDATE_1196 = "/sql/common/dialog-update-1196.sql";
    public static final String DIALOG_UPDATE_1202 = "/sql/common/dialog-update-1202.sql";
    public static final String DIALOG_UPDATE_1224 = "/sql/common/dialog-update-1224.sql";
    public static final String DIALOG_UPDATE_1277 = "/sql/common/dialog-update-1277.sql";
    public static final String SUPPORT_USER = "/sql/common/support-user.sql";
    public static final String LOCATIONS = "/sql/common/locations.sql";
    public static final String FAVORITE_PLACES = "/sql/common/favorite-places.sql";
    public static final String SUBSCRIPTIONS_PREMIUM = "/sql/common/subscriptions-premium.sql";

    private SqlScripts() {
    }
}
