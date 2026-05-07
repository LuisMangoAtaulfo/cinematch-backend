package com.cinematchbackend.dto.request;

import com.cinematchbackend.enums.GeneroContenido;
import com.cinematchbackend.enums.TipoContenido;
import lombok.Data;

@Data
public class FiltroRequestDTO {
    private Long salaId;
    private TipoContenido tipo;
    private GeneroContenido genero;
    private String plataforma;
}