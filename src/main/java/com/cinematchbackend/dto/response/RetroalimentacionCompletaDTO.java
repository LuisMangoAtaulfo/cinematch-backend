package com.cinematchbackend.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class RetroalimentacionCompletaDTO {
    private Double promedio;
    private List<Integer> distribucion; // array de 10 elementos (count por rating 1-10)
    private Integer totalCalificaciones;
    private List<CalificacionResponseDTO> calificaciones;
    private Boolean filtroActivo;
}
