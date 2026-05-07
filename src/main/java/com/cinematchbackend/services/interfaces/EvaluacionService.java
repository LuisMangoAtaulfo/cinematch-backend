package com.cinematchbackend.services.interfaces;

import com.cinematchbackend.dto.request.EvaluacionRequestDTO;

public interface EvaluacionService {
    void registrarEvaluacion(EvaluacionRequestDTO dto);
    boolean compararEvaluaciones(Long salaId, String contenidoId);
}