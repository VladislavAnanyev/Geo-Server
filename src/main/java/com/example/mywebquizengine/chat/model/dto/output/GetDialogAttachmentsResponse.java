package com.example.mywebquizengine.chat.model.dto.output;

import com.example.mywebquizengine.common.model.SuccessfulResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetDialogAttachmentsResponse extends SuccessfulResponse {
    private GetDialogAttachmentsResult result;
}
