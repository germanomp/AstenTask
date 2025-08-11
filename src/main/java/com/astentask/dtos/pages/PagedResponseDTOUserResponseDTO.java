package com.astentask.dtos.pages;

import com.astentask.dtos.UserResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PagedResponseUserResponseDTO", description = "Resposta paginada com lista de UserResponseDTO")
public class PagedResponseDTOUserResponseDTO extends PagedResponseDTO<UserResponseDTO>{
}
