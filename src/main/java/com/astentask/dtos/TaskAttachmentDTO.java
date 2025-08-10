package com.astentask.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskAttachmentDTO {
    private Long id;
    private String filename;
    private String fileType;
}
