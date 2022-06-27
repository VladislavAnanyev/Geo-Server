package com.example.mywebquizengine.common.rabbit;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = CustomEnumDeserializer.class)
public interface Type {


}
