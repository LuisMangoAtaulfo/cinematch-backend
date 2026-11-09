package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.response.SalaResponseDTO;
import com.cinematchbackend.services.interfaces.SalaService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class SalaGraphQLController {

    private final SalaService salaService;

    @QueryMapping
    public SalaResponseDTO obtenerSala(@Argument Long id) {
        return salaService.obtenerSala(id);
    }
}
