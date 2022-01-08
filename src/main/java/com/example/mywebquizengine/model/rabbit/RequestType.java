package com.example.mywebquizengine.model.rabbit;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public enum RequestType implements Type {
    REQUEST, ACCEPT_REQUEST, REJECT_REQUEST;
}
