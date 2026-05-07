package com.cinematchbackend.dto.request;

import lombok.Data;

@Data
public class CalificacionRequestDTO {
    private Long salaId;
    private Long usuarioId;
    private Integer valor;
}