package com.example.meetings.chat.model.dto.output;

import com.example.meetings.chat.model.LastDialogDTO;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class GetDialogsResult {
    private List<LastDialogDTO> dialogs;
}
