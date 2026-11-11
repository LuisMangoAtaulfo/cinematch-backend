package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.request.FiltroRequestDTO;
import com.cinematchbackend.dto.response.ContenidoResponseDTO;
import com.cinematchbackend.dto.response.FiltroResultadoDTO;
import com.cinematchbackend.repositories.ContenidoRepository;
import com.cinematchbackend.services.interfaces.FiltroService;
import com.cinematchbackend.services.interfaces.PlataformaService;
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
    private final PlataformaService plataformaService;

    @QueryMapping
    public List<ContenidoResponseDTO> contenidoPorSala(@Argument Long salaId) {
        return filtroService.obtenerContenidoPorSala(salaId)
                .orElse(Collections.emptyList());
    }

    @QueryMapping
    public FiltroResultadoDTO contenidoFiltrado(@Argument FiltroRequestDTO input) {
        log.info("Consultando contenido filtrado para sala {}", input.getSalaId());
        return filtroService.procesarFiltrosConTotal(input);
    }

    @QueryMapping
    public FiltroResultadoDTO contenidosFiltrados(@Argument FiltroRequestDTO input) {
        log.info("Consulta pública de contenido filtrado");
        return filtroService.filtradoPublico(input);
    }

    @QueryMapping
    public List<String> generosDisponibles() {
        return filtroService.obtenerGenerosDisponibles();
    }

    @QueryMapping
    public List<Integer> aniosDisponibles() {
        return plataformaService.obtenerAniosDisponibles();
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public List<ContenidoResponseDTO> aplicarFiltros(@Argument FiltroRequestDTO input) {
        log.info("Aplicando filtros para sala {}", input.getSalaId());
        return filtroService.procesarFiltros(input);
    }
}
