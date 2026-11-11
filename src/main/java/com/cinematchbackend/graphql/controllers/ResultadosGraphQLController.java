package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.response.ResultadoSalaDTO;
import com.cinematchbackend.services.interfaces.SalaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ResultadosGraphQLController {

    private final SalaService salaService;

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public ResultadoSalaDTO obtenerResultados(@Argument Long salaId) {
        log.info("Obteniendo resultados de sala {}", salaId);
        return salaService.obtenerResultados(salaId);
    }
}
