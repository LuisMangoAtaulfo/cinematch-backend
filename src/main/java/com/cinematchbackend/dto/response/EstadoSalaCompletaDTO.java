package com.cinematchbackend.dto.response;

import com.cinematchbackend.enums.EstadoSala;
import lombok.Data;

import java.util.List;

@Data
public class EstadoSalaCompletaDTO {
    private Long salaId;
    private String codigo;
    private EstadoSala estado;
    private List<EstadoConexionDTO> estadosUsuarios;
}
