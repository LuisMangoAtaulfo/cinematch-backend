package com.cinematchbackend.controllers;

import com.cinematchbackend.dto.response.MetricasResponseDTO;
import com.cinematchbackend.services.interfaces.MetricasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/metricas")
@RequiredArgsConstructor
public class MetricasController {

    private final MetricasService metricasService;

    @GetMapping
    public ResponseEntity<MetricasResponseDTO> obtenerMetricas() {
        return ResponseEntity.ok(metricasService.procesarMetricas());
    }
}