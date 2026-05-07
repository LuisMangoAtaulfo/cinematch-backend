package com.cinematchbackend.controllers;

import com.cinematchbackend.dto.response.CalificacionResponseDTO;
import com.cinematchbackend.services.interfaces.RetroalimentacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/retroalimentacion")
@RequiredArgsConstructor
public class RetroalimentacionController {

    private final RetroalimentacionService retroalimentacionService;

    @GetMapping
    public ResponseEntity<List<CalificacionResponseDTO>> obtenerRetroalimentacion() {
        return ResponseEntity.ok(retroalimentacionService.procesarRetroalimentacion());
    }

    @GetMapping("/promedio")
    public ResponseEntity<Double> obtenerPromedio() {
        return ResponseEntity.ok(retroalimentacionService.obtenerPromedio());
    }
}