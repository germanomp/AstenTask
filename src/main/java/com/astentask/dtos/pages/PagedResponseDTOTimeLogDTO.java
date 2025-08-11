package com.astentask.dtos.pages;

import com.astentask.dtos.TimeLogDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PagedResponseTimeLogDTO", description = "Resposta paginada com lista de TimeLogDTO")
public class PagedResponseDTOTimeLogDTO extends PagedResponseDTO<TimeLogDTO>{
}
