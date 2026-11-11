package com.cinematchbackend.services.interfaces;

import com.cinematchbackend.dto.request.CalificacionRequestDTO;

public interface CalificacionService {
    void registrarCalificacion(CalificacionRequestDTO dto);
    void calificarExperiencia(CalificacionRequestDTO dto);
}
