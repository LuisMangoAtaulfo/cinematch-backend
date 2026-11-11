package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.request.LoginRequestDTO;
import com.cinematchbackend.dto.request.UsuarioRegistroDTO;
import com.cinematchbackend.dto.response.AuthResponseDTO;
import com.cinematchbackend.entities.UsuarioEntidad;
import com.cinematchbackend.exceptions.CamposVaciosException;
import com.cinematchbackend.services.interfaces.AdministradorService;
import com.cinematchbackend.services.interfaces.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthGraphQLController {

    private final UsuarioService usuarioService;
    private final AdministradorService administradorService;

    @MutationMapping
    public AuthResponseDTO login(@Argument LoginRequestDTO input) {
        // R-BE-01: Validar campos vacíos
        if (input.getCorreo() == null || input.getCorreo().isBlank() ||
            input.getPassword() == null || input.getPassword().isBlank()) {
            throw new CamposVaciosException();
        }

        String token = usuarioService.validarCredenciales(input.getCorreo(), input.getPassword());
        String nombre = usuarioService.obtenerNombreUsuarioLogeado(input.getCorreo());

        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setCorreo(input.getCorreo());
        response.setNombre(nombre);

        log.info("Login GraphQL exitoso: {}", input.getCorreo());
        return response;
    }

    @MutationMapping
    public AuthResponseDTO registrar(@Argument UsuarioRegistroDTO input) {
        // R-BE-01: Validar campos vacíos
        if (input.getNombre() == null || input.getNombre().isBlank() ||
            input.getCorreo() == null || input.getCorreo().isBlank() ||
            input.getPassword() == null || input.getPassword().isBlank()) {
            throw new CamposVaciosException();
        }

        UsuarioEntidad usuario = usuarioService.crearUsuario(input);
        String token = usuarioService.validarCredenciales(input.getCorreo(), input.getPassword());

        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setCorreo(input.getCorreo());
        response.setNombre(usuario.getNombre());

        log.info("Registro GraphQL exitoso: {}", input.getCorreo());
        return response;
    }

    @MutationMapping
    public AuthResponseDTO loginAdmin(@Argument LoginRequestDTO input) {
        // R-BE-01: Validar campos vacíos
        if (input.getCorreo() == null || input.getCorreo().isBlank() ||
            input.getPassword() == null || input.getPassword().isBlank()) {
            throw new CamposVaciosException();
        }

        String token = administradorService.validarCredenciales(input.getCorreo(), input.getPassword());

        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setCorreo(input.getCorreo());

        log.info("Login admin GraphQL exitoso: {}", input.getCorreo());
        return response;
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        usuarioService.invalidarToken(token);
        log.info("Logout GraphQL exitoso");
        return true;
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Boolean logoutAdmin(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        administradorService.invalidarToken(token);
        log.info("Logout admin GraphQL exitoso");
        return true;
    }
}
