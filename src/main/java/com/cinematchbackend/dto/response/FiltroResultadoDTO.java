package com.cinematchbackend.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class FiltroResultadoDTO {
    private List<ContenidoResponseDTO> contenidos;
    private Integer totalResultados;
}
