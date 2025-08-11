package com.astentask.dtos.pages;

import com.astentask.dtos.TimeLogDTO;
import com.astentask.dtos.UserResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PagedResponseTimeLogDTO", description = "Resposta paginada com lista de TimeLogDTO")
public class PagedResponseDTOTimeLogResponseDTO extends PagedResponseDTO<TimeLogDTO>{
}
