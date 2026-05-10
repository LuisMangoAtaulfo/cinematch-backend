package com.cinematchbackend.controllers;

import com.cinematchbackend.dto.request.SalaRequestDTO;
import com.cinematchbackend.dto.request.UnirseSalaDTO;
import com.cinematchbackend.dto.response.MatchResponseDTO;
import com.cinematchbackend.dto.response.SalaResponseDTO;
import com.cinematchbackend.services.interfaces.SalaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salas")
@RequiredArgsConstructor
public class SalaController {

    private final SalaService salaService;

    @PostMapping("/crear")
    public ResponseEntity<SalaResponseDTO> crearSala(@RequestBody SalaRequestDTO dto) {
        return ResponseEntity.ok(salaService.crearSala(dto));
    }

    @PostMapping("/unirse")
    public ResponseEntity<SalaResponseDTO> unirseASala(@RequestBody UnirseSalaDTO dto) {
        return ResponseEntity.ok(salaService.unirseASala(dto));
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<Void> finalizarSala(@PathVariable Long id) {
        salaService.finalizarSala(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/matches")
    public ResponseEntity<List<MatchResponseDTO>> obtenerMatches(@PathVariable Long id) {
        return ResponseEntity.ok(salaService.obtenerMatches(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalaResponseDTO> obtenerSala(@PathVariable Long id) {
        return ResponseEntity.ok(salaService.obtenerSala(id));
    }
}