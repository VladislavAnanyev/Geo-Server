package com.example.mywebquizengine.auth.model.dto.output;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserExistDto {
    private boolean exist;
}
