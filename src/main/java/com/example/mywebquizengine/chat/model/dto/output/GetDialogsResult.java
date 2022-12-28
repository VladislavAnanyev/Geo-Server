package com.example.mywebquizengine.chat.model.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetDialogsResult {
    private List<LastDialog> dialogs;
}
