package com.cinematchbackend.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchResponseDTO {
    private Long id;
    private LocalDateTime fechaDeteccion;
    private ContenidoResponseDTO contenido;
}