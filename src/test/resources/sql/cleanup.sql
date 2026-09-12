TRUNCATE TABLE
    message_status_history,
    messages_photos,
    messages_forwarded,
    messages,
    plans_responses,
    plans,
    location_sharing,
    content_reports,
    users_friends,
    users_dialogs,
    requests,
    meetings,
    geolocation_history,
    geolocations,
    geolocation_exclude_zones,
    locations,
    user_favorite_places,
    user_subscriptions,
    user_roles,
    users_photos,
    device,
    users,
    dialogs
    RESTART IDENTITY CASCADE;

ALTER SEQUENCE IF EXISTS hibernate_sequence RESTART WITH 1;
