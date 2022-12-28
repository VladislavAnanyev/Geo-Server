package com.example.mywebquizengine.request;

import com.example.mywebquizengine.request.model.dto.output.RequestView;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Модель с информацией об отправленных пользователем заявках
 */
@Data
@Accessors(chain = true)
public class GetSentFromUserRequestsResult {
    private List<RequestView> requests;
}
