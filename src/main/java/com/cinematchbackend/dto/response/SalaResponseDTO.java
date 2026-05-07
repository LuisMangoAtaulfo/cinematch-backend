package com.cinematchbackend.dto.response;

import com.cinematchbackend.enums.EstadoSala;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SalaResponseDTO {
    private Long id;
    private String codigo;
    private EstadoSala estado;
    private LocalDateTime fechaCreacion;
    private UsuarioResponseDTO usuario1;
    private UsuarioResponseDTO usuario2;
}