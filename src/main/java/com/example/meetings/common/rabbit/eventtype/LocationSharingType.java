package com.example.meetings.common.rabbit.eventtype;

/**
 * Типы событий Rabbit, связанные с обменом геолокацией.
 */
public enum LocationSharingType implements Type {
    /** Создано приглашение на обмен геолокацией. */
    LOCATION_SHARING_REQUEST,
    /** Ожидающее приглашение принято, обмен стал активным. */
    LOCATION_SHARING_ACCEPTED
}
