package com.cinematchbackend.dto.response;

import lombok.Data;

@Data
public class PresenciaDTO {
    private Long usuarioId;
    private String estado; // "CONECTADO" | "DESCONECTADO"
    private String nombreUsuario;
}