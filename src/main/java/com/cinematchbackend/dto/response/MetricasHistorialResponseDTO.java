package com.cinematchbackend.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MetricasHistorialResponseDTO {
    private LocalDateTime fecha;
    private Long totalSalas;
    private Long salasActivas;
    private Long totalMatches;
    private Double promedioCalificacion;
}