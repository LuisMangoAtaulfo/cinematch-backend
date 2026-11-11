package com.cinematchbackend.services.interfaces;

import com.cinematchbackend.dto.response.CalificacionResponseDTO;
import com.cinematchbackend.dto.response.RetroalimentacionCompletaDTO;

import java.util.List;

public interface RetroalimentacionService {
    List<CalificacionResponseDTO> procesarRetroalimentacion();
    Double obtenerPromedio();
    RetroalimentacionCompletaDTO obtenerRetroalimentacionCompleta(String desde, String hasta);
}
