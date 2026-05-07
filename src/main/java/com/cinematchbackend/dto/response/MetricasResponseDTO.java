package com.cinematchbackend.dto.response;

import lombok.Data;

@Data
public class MetricasResponseDTO {
    private Long totalSalas;
    private Long salasActivas;
    private Long totalMatches;
    private Double promedioCalificacion;
}