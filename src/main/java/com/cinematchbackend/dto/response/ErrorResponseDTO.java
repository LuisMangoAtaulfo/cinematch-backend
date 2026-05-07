package com.cinematchbackend.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponseDTO {
    private String mensaje;
    private Integer status;
    private LocalDateTime timestamp;
}