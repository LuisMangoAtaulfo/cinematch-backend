package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.request.PlataformaUpdateDTO;
import com.cinematchbackend.dto.response.PlataformaResponseDTO;
import com.cinematchbackend.services.interfaces.PlataformaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
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

    @MutationMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Boolean cambiarEstadoPlataforma(@Argument PlataformaUpdateDTO input) {
        log.info("Cambiando estado de plataforma {} a {}", input.getId(), input.getHabilitada());
        plataformaService.actualizarEstado(input);
        return true;
    }
}
