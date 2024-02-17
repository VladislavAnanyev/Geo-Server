package com.example.meetings.chat.model.dto.output;

import com.example.meetings.chat.model.FileResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Информация о вложениях в сообщениях диалога
 */
@Data
@AllArgsConstructor
public class GetDialogAttachmentsResult {
    private List<FileResponse> attachments;
}
