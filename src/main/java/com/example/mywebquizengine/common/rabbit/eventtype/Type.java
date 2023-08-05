package com.example.mywebquizengine.common.rabbit.eventtype;

import com.example.mywebquizengine.common.rabbit.CustomEnumDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = CustomEnumDeserializer.class)
public interface Type {}
