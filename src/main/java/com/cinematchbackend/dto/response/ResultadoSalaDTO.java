package com.cinematchbackend.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ResultadoSalaDTO {
    private Long salaId;
    private List<MatchResponseDTO> matches;
    private Integer totalMatches;
    private Integer totalEvaluaciones;
    private Integer totalMensajes;
}
