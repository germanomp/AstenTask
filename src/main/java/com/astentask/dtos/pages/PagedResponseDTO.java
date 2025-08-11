package com.astentask.dtos.pages;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta paginada genérica")
public class PagedResponseDTO<T> {

    @Schema(description = "Lista de itens da página")
    private List<T> content;

    @Schema(description = "Número da página atual (zero-based)", example = "0")
    private int pageNumber;

    @Schema(description = "Tamanho da página", example = "10")
    private int pageSize;

    @Schema(description = "Número total de elementos", example = "100")
    private long totalElements;

    @Schema(description = "Número total de páginas", example = "10")
    private int totalPages;

    @Schema(description = "Indica se é a última página", example = "false")
    private boolean last;
}