package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.response.MetricasHistorialResponseDTO;
import com.cinematchbackend.dto.response.MetricasResponseDTO;
import com.cinematchbackend.dto.response.MetricasUsuarioResponseDTO;
import com.cinematchbackend.services.interfaces.MetricasService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MetricasGraphQLController {

    private final MetricasService metricasService;

    @QueryMapping
    public MetricasResponseDTO metricas() {
        return metricasService.procesarMetricas();
    }

    @QueryMapping
    public List<MetricasHistorialResponseDTO> historialMetricas() {
        return metricasService.obtenerHistorial();
    }

    @QueryMapping
    public MetricasUsuarioResponseDTO metricasUsuario(@Argument Long id) {
        return metricasService.procesarMetricasUsuario(id);
    }

    @QueryMapping
    public List<MetricasHistorialResponseDTO> historialMetricasUsuario(@Argument Long id) {
        return metricasService.obtenerHistorialUsuario(id);
    }
}
