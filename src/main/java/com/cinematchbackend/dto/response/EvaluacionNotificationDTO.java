package com.cinematchbackend.dto.response;

import lombok.Data;

@Data
public class EvaluacionNotificationDTO {
    private Long salaId;
    private Long usuarioId;
    private String contenidoId;
    private Boolean decision; // true = LIKE, false = DISLIKE
    private String nombreUsuario;
}
