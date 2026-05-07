package com.cinematchbackend.controllers;

import com.cinematchbackend.dto.request.PlataformaUpdateDTO;
import com.cinematchbackend.dto.response.PlataformaResponseDTO;
import com.cinematchbackend.services.interfaces.PlataformaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plataformas")
@RequiredArgsConstructor
public class PlataformasController {

    private final PlataformaService plataformaService;

    @GetMapping
    public ResponseEntity<List<PlataformaResponseDTO>> obtenerPlataformas() {
        return ResponseEntity.ok(plataformaService.listarPlataformas());
    }

    @PutMapping("/estado")
    public ResponseEntity<Void> cambiarEstado(@RequestBody PlataformaUpdateDTO dto) {
        plataformaService.actualizarEstado(dto);
        return ResponseEntity.ok().build();
    }
}