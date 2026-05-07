package com.cinematchbackend.controllers;

import com.cinematchbackend.dto.response.MatchResponseDTO;
import com.cinematchbackend.services.interfaces.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/{salaId}")
    public ResponseEntity<List<MatchResponseDTO>> obtenerMatches(@PathVariable Long salaId) {
        return ResponseEntity.ok(matchService.buscarMatches(salaId));
    }
}