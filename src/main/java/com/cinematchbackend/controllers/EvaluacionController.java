package com.cinematchbackend.controllers;

import com.cinematchbackend.dto.request.EvaluacionRequestDTO;
import com.cinematchbackend.services.interfaces.EvaluacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/evaluaciones")
@RequiredArgsConstructor
public class EvaluacionController {

    private final EvaluacionService evaluacionService;

    @PostMapping
    public ResponseEntity<Void> evaluarContenido(@RequestBody EvaluacionRequestDTO dto) {
        evaluacionService.registrarEvaluacion(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/comparar")
    public ResponseEntity<Boolean> compararEvaluaciones(
            @RequestParam Long salaId,
            @RequestParam String contenidoId) {
        return ResponseEntity.ok(evaluacionService.compararEvaluaciones(salaId, contenidoId));
    }
}