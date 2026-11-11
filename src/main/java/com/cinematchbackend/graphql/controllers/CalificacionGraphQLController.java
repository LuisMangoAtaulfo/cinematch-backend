package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.request.CalificacionRequestDTO;
import com.cinematchbackend.services.interfaces.CalificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CalificacionGraphQLController {

    private final CalificacionService calificacionService;

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean registrarCalificacion(@Argument CalificacionRequestDTO input) {
        calificacionService.registrarCalificacion(input);
        log.info("Calificación registrada para sala {} por usuario {}", input.getSalaId(), input.getUsuarioId());
        return true;
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean calificarExperiencia(@Argument CalificacionRequestDTO input) {
        calificacionService.calificarExperiencia(input);
        log.info("Experiencia calificada para sala {} por usuario {} con valor {}",
                input.getSalaId(), input.getUsuarioId(), input.getValor());
        return true;
    }
}
