package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.request.EvaluacionRequestDTO;
import com.cinematchbackend.services.interfaces.EvaluacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EvaluacionGraphQLController {

    private final EvaluacionService evaluacionService;

    @QueryMapping
    public Boolean compararEvaluaciones(@Argument Long salaId, @Argument String contenidoId) {
        return evaluacionService.compararEvaluaciones(salaId, contenidoId);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean evaluarContenido(@Argument EvaluacionRequestDTO input) {
        log.info("Evaluando contenido {} en sala {} por usuario {}", input.getContenidoId(), input.getSalaId(), input.getUsuarioId());
        evaluacionService.registrarEvaluacion(input);
        return true;
    }
}
