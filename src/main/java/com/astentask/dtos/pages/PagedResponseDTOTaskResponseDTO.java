package com.astentask.dtos.pages;

import com.astentask.dtos.TaskResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PagedResponseTaskResponseDTO", description = "Resposta paginada com lista de TaskResponseDTO")
public class PagedResponseDTOTaskResponseDTO extends PagedResponseDTO<TaskResponseDTO>{
}
