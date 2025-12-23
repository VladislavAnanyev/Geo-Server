package com.example.meetings.chat.model.dto.output;

import lombok.*;

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
