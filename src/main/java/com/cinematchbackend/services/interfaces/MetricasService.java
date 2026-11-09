package com.cinematchbackend.services.interfaces;

import com.cinematchbackend.dto.response.MetricasHistorialResponseDTO;
import com.cinematchbackend.dto.response.MetricasResponseDTO;
import com.cinematchbackend.dto.response.MetricasUsuarioResponseDTO;

import java.util.List;

public interface MetricasService {
    MetricasResponseDTO procesarMetricas();
    List<MetricasHistorialResponseDTO> obtenerHistorial();
    MetricasUsuarioResponseDTO procesarMetricasUsuario(Long usuarioId);
    List<MetricasHistorialResponseDTO> obtenerHistorialUsuario(Long usuarioId);
}