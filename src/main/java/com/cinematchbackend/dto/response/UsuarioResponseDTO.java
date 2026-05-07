package com.cinematchbackend.dto.response;

import com.cinematchbackend.enums.RolUsuario;
import lombok.Data;

@Data
public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String correo;
    private String descripcion;
    private RolUsuario rol;
}