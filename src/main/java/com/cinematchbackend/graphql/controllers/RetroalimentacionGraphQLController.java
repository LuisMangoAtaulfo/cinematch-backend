package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.response.CalificacionResponseDTO;
import com.cinematchbackend.services.interfaces.RetroalimentacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RetroalimentacionGraphQLController {

    private final RetroalimentacionService retroalimentacionService;

    @QueryMapping
    public List<CalificacionResponseDTO> retroalimentacion() {
        return retroalimentacionService.procesarRetroalimentacion();
    }

    @QueryMapping
    public Double promedioCalificacion() {
        return retroalimentacionService.obtenerPromedio();
    }
}
