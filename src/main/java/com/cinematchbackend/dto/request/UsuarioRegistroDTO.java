package com.cinematchbackend.dto.request;

import lombok.Data;

@Data
public class UsuarioRegistroDTO {
    private String nombre;
    private String correo;
    private String password;
}