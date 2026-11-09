package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.request.PerfilUpdateDTO;
import com.cinematchbackend.dto.response.UsuarioResponseDTO;
import com.cinematchbackend.services.interfaces.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UsuarioGraphQLController {

    private final UsuarioService usuarioService;

    @QueryMapping
    public UsuarioResponseDTO obtenerPerfil(@Argument Long id) {
        return usuarioService.obtenerDatosUsuarioLogeado(id);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public UsuarioResponseDTO actualizarPerfil(@Argument Long id, @Argument PerfilUpdateDTO input) {
        log.info("Actualizando perfil de usuario {}", id);
        return usuarioService.actualizarPerfil(id, input);
    }
}
