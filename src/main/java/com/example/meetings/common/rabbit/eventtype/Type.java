package com.example.meetings.common.rabbit.eventtype;

import com.example.meetings.common.rabbit.CustomEnumDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = CustomEnumDeserializer.class)
public interface Type {
}
