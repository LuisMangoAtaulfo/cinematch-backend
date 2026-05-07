package com.cinematchbackend.dto.response;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String token;
    private Long id;
    private String nombre;
    private String correo;
}