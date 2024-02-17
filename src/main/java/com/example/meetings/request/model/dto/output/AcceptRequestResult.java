package com.example.meetings.request.model.dto.output;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AcceptRequestResult {
    /**
     * Идентификатор диалога, который создается после принятия заявки между получателем и отправителем заявки
     */
    private Long dialogId;
}
