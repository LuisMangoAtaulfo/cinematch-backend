package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.response.MetricasHistorialResponseDTO;
import com.cinematchbackend.dto.response.MetricasResponseDTO;
import com.cinematchbackend.services.interfaces.MetricasService;
import lombok.RequiredArgsConstructor;
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
}
