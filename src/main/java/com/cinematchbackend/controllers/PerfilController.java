package com.cinematchbackend.controllers;

import com.cinematchbackend.dto.request.PerfilUpdateDTO;
import com.cinematchbackend.dto.response.UsuarioResponseDTO;
import com.cinematchbackend.services.interfaces.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final UsuarioService usuarioService;

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarPerfil(
            @PathVariable Long id,
            @RequestBody PerfilUpdateDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarPerfil(id, dto));
    }
}