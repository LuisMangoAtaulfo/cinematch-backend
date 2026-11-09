package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.request.FiltroRequestDTO;
import com.cinematchbackend.dto.response.ContenidoResponseDTO;
import com.cinematchbackend.services.interfaces.FiltroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class FiltroGraphQLController {

    private final FiltroService filtroService;

    @QueryMapping
    public List<ContenidoResponseDTO> contenidoPorSala(@Argument Long salaId) {
        return filtroService.obtenerContenidoPorSala(salaId)
                .orElse(Collections.emptyList());
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public List<ContenidoResponseDTO> aplicarFiltros(@Argument FiltroRequestDTO input) {
        log.info("Aplicando filtros para sala {}", input.getSalaId());
        return filtroService.procesarFiltros(input);
    }
}
