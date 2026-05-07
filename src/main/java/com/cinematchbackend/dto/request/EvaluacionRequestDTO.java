package com.cinematchbackend.dto.request;

import lombok.Data;

@Data
public class EvaluacionRequestDTO {
    private Long salaId;
    private Long usuarioId;
    private String contenidoId;
    private Boolean decision;
}