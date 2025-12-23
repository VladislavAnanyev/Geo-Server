package com.example.meetings.chat.model.dto.output;

import com.example.meetings.chat.model.LastDialogDTO;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetDialogsResult {
    private List<LastDialogDTO> dialogs;
}
