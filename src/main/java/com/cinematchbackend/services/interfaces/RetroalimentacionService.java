package com.cinematchbackend.services.interfaces;

import com.cinematchbackend.dto.response.CalificacionResponseDTO;

import java.util.List;

public interface RetroalimentacionService {
    List<CalificacionResponseDTO> procesarRetroalimentacion();
    Double obtenerPromedio();
}