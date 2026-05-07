package com.cinematchbackend.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CalificacionResponseDTO {
    private Long id;
    private Integer valor;
    private LocalDateTime fecha;
    private Long usuarioId;
    private Long salaId;
}