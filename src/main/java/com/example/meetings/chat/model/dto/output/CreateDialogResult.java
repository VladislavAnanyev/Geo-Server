package com.example.meetings.chat.model.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Информация о созданном диалоге
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDialogResult {
    /**
     * Идентификатор диалога
     */
    private Long dialogId;
}
