package com.cinematchbackend.dto.response;

import lombok.Data;

@Data
public class EstadoConexionDTO {
    private Long usuarioId;
    private Boolean conectado;
    private String nombreUsuario;
}
