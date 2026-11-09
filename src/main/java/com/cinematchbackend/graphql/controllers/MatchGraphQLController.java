package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.response.MatchResponseDTO;
import com.cinematchbackend.services.interfaces.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Resolver GraphQL para queries de matches.
 * Optimizado para usar un solo service y evitar duplicación de lógica.
 */
@Controller
@RequiredArgsConstructor
public class MatchGraphQLController {

    private final MatchService matchService;

    @QueryMapping
    public List<MatchResponseDTO> buscarMatches(@Argument Long salaId) {
        return matchService.buscarMatches(salaId);
    }

    @QueryMapping
    public List<MatchResponseDTO> obtenerMatchesSala(@Argument Long id) {
        // Reutiliza el mismo método para evitar duplicación
        return matchService.buscarMatches(id);
    }
}
