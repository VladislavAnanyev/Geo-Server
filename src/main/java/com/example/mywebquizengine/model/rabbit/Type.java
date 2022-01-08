package com.example.mywebquizengine.model.rabbit;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = CustomEnumDeserializer.class)
public interface Type {


}
