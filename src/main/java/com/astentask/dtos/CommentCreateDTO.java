package com.astentask.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateDTO {
    @NotBlank(message = "Comentário não pode ser vazio")
    @Size(max = 1000)
    private String content;
}