package com.cinematchbackend.controllers;

import com.cinematchbackend.dto.request.LoginRequestDTO;
import com.cinematchbackend.dto.request.UsuarioRegistroDTO;
import com.cinematchbackend.dto.response.AuthResponseDTO;
import com.cinematchbackend.entities.UsuarioEntidad;
import com.cinematchbackend.services.interfaces.AdministradorService;
import com.cinematchbackend.services.interfaces.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final AdministradorService administradorService;

    @PostMapping("/registrar")
    public ResponseEntity<AuthResponseDTO> registrar(@RequestBody UsuarioRegistroDTO dto) {
        UsuarioEntidad usuario = usuarioService.crearUsuario(dto);
        String token = usuarioService.validarCredenciales(dto.getCorreo(), dto.getPassword());
        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setNombre(usuario.getNombre());
        response.setCorreo(usuario.getCorreo());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> iniciarSesion(@RequestBody LoginRequestDTO dto) {
        String token = usuarioService.validarCredenciales(dto.getCorreo(), dto.getPassword());
        String nombreUsuario = usuarioService.obtenerNombreUsuarioLogeado(dto.getCorreo());
        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setCorreo(dto.getCorreo());
        response.setNombre(nombreUsuario);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/admin")
    public ResponseEntity<AuthResponseDTO> iniciarSesionAdmin(@RequestBody LoginRequestDTO dto) {
        String token = administradorService.validarCredenciales(dto.getCorreo(), dto.getPassword());
        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setCorreo(dto.getCorreo());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> cerrarSesion(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        usuarioService.invalidarToken(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout/admin")
    public ResponseEntity<Void> cerrarSesionAdmin(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        administradorService.invalidarToken(token);
        return ResponseEntity.ok().build();
    }
}