package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.response.PlataformaResponseDTO;
import com.cinematchbackend.services.interfaces.PlataformaService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PlataformaGraphQLController {

    private final PlataformaService plataformaService;

    @QueryMapping
    public List<PlataformaResponseDTO> plataformas() {
        return plataformaService.listarPlataformas();
    }

    @QueryMapping
    public Boolean plataformaEnUso(@Argument Long id) {
        return plataformaService.estaEnUso(id);
    }
}
