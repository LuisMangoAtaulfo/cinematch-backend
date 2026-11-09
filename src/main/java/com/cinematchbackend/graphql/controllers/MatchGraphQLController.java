package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.response.MatchResponseDTO;
import com.cinematchbackend.services.interfaces.MatchService;
import com.cinematchbackend.services.interfaces.SalaService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MatchGraphQLController {

    private final MatchService matchService;
    private final SalaService salaService;

    @QueryMapping
    public List<MatchResponseDTO> buscarMatches(@Argument Long salaId) {
        return matchService.buscarMatches(salaId);
    }

    @QueryMapping
    public List<MatchResponseDTO> obtenerMatchesSala(@Argument Long id) {
        return salaService.obtenerMatches(id);
    }
}
