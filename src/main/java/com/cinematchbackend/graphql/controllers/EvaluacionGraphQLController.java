package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.services.interfaces.EvaluacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class EvaluacionGraphQLController {

    private final EvaluacionService evaluacionService;

    @QueryMapping
    public Boolean compararEvaluaciones(@Argument Long salaId, @Argument String contenidoId) {
        return evaluacionService.compararEvaluaciones(salaId, contenidoId);
    }
}
