package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.response.MatchResponseDTO;
import com.cinematchbackend.services.interfaces.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

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
        return matchService.buscarMatches(id);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public List<MatchResponseDTO> obtenerMatchesUsuario(@Argument Long usuarioId) {
        return matchService.obtenerMatchesUsuario(usuarioId);
    }
}
