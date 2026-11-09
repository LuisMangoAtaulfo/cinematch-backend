package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.response.UsuarioResponseDTO;
import com.cinematchbackend.services.interfaces.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UsuarioGraphQLController {

    private final UsuarioService usuarioService;

    @QueryMapping
    public UsuarioResponseDTO obtenerPerfil(@Argument Long id) {
        return usuarioService.obtenerDatosUsuarioLogeado(id);
    }
}
