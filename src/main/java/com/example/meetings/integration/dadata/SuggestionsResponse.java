package com.example.meetings.integration.dadata;

import com.example.meetings.common.model.SuccessfulResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain=true)
public class SuggestionsResponse extends SuccessfulResponse {
    private DaDataSuggestions result;
}
