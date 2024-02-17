package com.example.meetings.chat.model.dto.output;

import com.example.meetings.common.model.SuccessfulResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetDialogAttachmentsResponse extends SuccessfulResponse {
    private GetDialogAttachmentsResult result;
}
