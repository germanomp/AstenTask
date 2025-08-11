package com.astentask.dtos.pages;

import com.astentask.dtos.CommentDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PagedResponseCommentDTO", description = "Resposta paginada com lista de CommentDTO")
public class PagedResponseDTOCommentDTO extends PagedResponseDTO<CommentDTO>{
}
