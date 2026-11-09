package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.response.ContenidoResponseDTO;
import com.cinematchbackend.services.interfaces.FiltroService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class FiltroGraphQLController {

    private final FiltroService filtroService;

    @QueryMapping
    public List<ContenidoResponseDTO> contenidoPorSala(@Argument Long salaId) {
        return filtroService.obtenerContenidoPorSala(salaId)
                .orElse(Collections.emptyList());
    }
}
