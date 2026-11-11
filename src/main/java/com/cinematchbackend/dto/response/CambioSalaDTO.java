package com.cinematchbackend.dto.response;

import com.cinematchbackend.enums.EstadoSala;
import lombok.Data;

@Data
public class CambioSalaDTO {
    private Long salaId;
    private EstadoSala estado;
    private String mensaje;
}
