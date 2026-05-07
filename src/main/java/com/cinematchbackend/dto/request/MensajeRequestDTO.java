package com.cinematchbackend.dto.request;

import lombok.Data;

@Data
public class MensajeRequestDTO {
    private Long salaId;
    private Long usuarioId;
    private String texto;
}