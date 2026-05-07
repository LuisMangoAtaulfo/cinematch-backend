package com.cinematchbackend.dto.request;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String correo;
    private String password;
}