package com.cinematchbackend.dto.response;

import com.cinematchbackend.enums.GeneroContenido;
import com.cinematchbackend.enums.TipoContenido;
import lombok.Data;

import java.util.List;

@Data
public class ContenidoResponseDTO {
    private String contenidoId;
    private String titulo;
    private Integer anio;
    private TipoContenido tipo;
    private GeneroContenido genero;
    private String imagen;
    private List<String> plataformas;
}