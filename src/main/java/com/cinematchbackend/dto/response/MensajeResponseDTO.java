package com.cinematchbackend.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MensajeResponseDTO {
    private Long id;
    private String texto;
    private LocalDateTime fechaEnvio;
    private Long usuarioId;
    private String nombreUsuario;
}