package com.cinematchbackend.dto.response;

import lombok.Data;

@Data
public class MetricasUsuarioResponseDTO {
    private Long totalSalas;
    private Long salasActivas;
    private Long totalMatches;
    private Double promedioCalificacion;
    private Long totalEvaluaciones;
}
