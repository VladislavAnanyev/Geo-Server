package com.example.meetings.integration.overpass;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Accessors(chain = true)
public class OverpassPoi {

    private String rawName;
    private String displayName;
    private Map<String, String> tags;

    public OverpassPoi setRawName(String rawName) {
        this.rawName = rawName;
        return this;
    }

    public OverpassPoi setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public OverpassPoi setTags(Map<String, String> tags) {
        this.tags = tags;
        return this;
    }
}
