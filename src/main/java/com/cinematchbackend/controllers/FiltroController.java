package com.cinematchbackend.controllers;

import com.cinematchbackend.dto.request.FiltroRequestDTO;
import com.cinematchbackend.dto.response.ContenidoResponseDTO;
import com.cinematchbackend.services.interfaces.FiltroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filtros")
@RequiredArgsConstructor
public class FiltroController {

    private final FiltroService filtroService;

    @PostMapping
    public ResponseEntity<List<ContenidoResponseDTO>> aplicarFiltros(@RequestBody FiltroRequestDTO dto) {
        return ResponseEntity.ok(filtroService.procesarFiltros(dto));
    }

    @GetMapping("/{salaId}")
    public ResponseEntity<List<ContenidoResponseDTO>> obtenerContenidoPorSala(@PathVariable Long salaId) {
        return filtroService.obtenerContenidoPorSala(salaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}