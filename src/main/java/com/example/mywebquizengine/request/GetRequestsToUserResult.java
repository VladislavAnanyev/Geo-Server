package com.example.mywebquizengine.request;

import com.example.mywebquizengine.request.model.dto.output.RequestView;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class GetRequestsToUserResult {
    private List<RequestView> requests;
}
