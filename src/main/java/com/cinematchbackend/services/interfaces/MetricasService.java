package com.cinematchbackend.services.interfaces;

import com.cinematchbackend.dto.response.MetricasHistorialResponseDTO;
import com.cinematchbackend.dto.response.MetricasResponseDTO;

import java.util.List;

public interface MetricasService {
    MetricasResponseDTO procesarMetricas();
    MetricasResponseDTO procesarMetricas(String desde, String hasta);
    List<MetricasHistorialResponseDTO> obtenerHistorial();
}
