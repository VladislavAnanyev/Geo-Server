package com.example.mywebquizengine.model.rabbit;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

public class CustomEnumDeserializer extends StdDeserializer<Type> {


    protected CustomEnumDeserializer(Class<?> vc) {
        super(vc);
    }

    protected CustomEnumDeserializer(JavaType valueType) {
        super(valueType);
    }

    protected CustomEnumDeserializer(StdDeserializer<?> src) {
        super(src);
    }

    public CustomEnumDeserializer() {
        super(String.class);
    }

    @Override
    public Type deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        String value = node.textValue();

        try {
            return MessageType.valueOf(value);
        } catch (IllegalArgumentException ignored) {}

        try {
            return RequestType.valueOf(value);
        } catch (IllegalArgumentException ignored) {}

        try {
            return MeetingType.valueOf(value);
        } catch (IllegalArgumentException ignored) {}

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}
