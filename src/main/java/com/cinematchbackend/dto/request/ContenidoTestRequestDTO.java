package com.cinematchbackend.dto.request;

import com.cinematchbackend.enums.GeneroContenido;
import com.cinematchbackend.enums.TipoContenido;
import lombok.Data;

/**
 * Input para insertar contenido de prueba (R-BE-01).
 */
@Data
public class ContenidoTestRequestDTO {
    private String contenidoId;
    private String titulo;
    private Integer anio;
    private TipoContenido tipo;
    private GeneroContenido genero;
    private String imagen;
    private String sinopsis;
}
