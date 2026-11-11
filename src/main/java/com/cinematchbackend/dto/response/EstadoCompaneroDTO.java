package com.cinematchbackend.dto.response;

import lombok.Data;

@Data
public class EstadoCompaneroDTO {
    private Long usuarioId;
    private String estado;
    private String nombreUsuario;
}
