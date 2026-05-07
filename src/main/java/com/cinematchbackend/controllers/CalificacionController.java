package com.cinematchbackend.controllers;

import com.cinematchbackend.dto.request.CalificacionRequestDTO;
import com.cinematchbackend.services.interfaces.CalificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calificaciones")
@RequiredArgsConstructor
public class CalificacionController {

    private final CalificacionService calificacionService;

    @PostMapping
    public ResponseEntity<Void> registrarCalificacion(@RequestBody CalificacionRequestDTO dto) {
        calificacionService.registrarCalificacion(dto);
        return ResponseEntity.ok().build();
    }
}