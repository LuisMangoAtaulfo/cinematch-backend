package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.response.CalificacionResponseDTO;
import com.cinematchbackend.dto.response.RetroalimentacionCompletaDTO;
import com.cinematchbackend.services.interfaces.RetroalimentacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RetroalimentacionGraphQLController {

    private final RetroalimentacionService retroalimentacionService;

    @QueryMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<CalificacionResponseDTO> retroalimentacion() {
        return retroalimentacionService.procesarRetroalimentacion();
    }

    @QueryMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Double promedioCalificacion() {
        return retroalimentacionService.obtenerPromedio();
    }

    @QueryMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public RetroalimentacionCompletaDTO obtenerRetroalimentacion(@Argument String desde, @Argument String hasta) {
        return retroalimentacionService.obtenerRetroalimentacionCompleta(desde, hasta);
    }
}
