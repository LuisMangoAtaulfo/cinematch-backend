package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.request.SalaRequestDTO;
import com.cinematchbackend.dto.request.UnirseSalaDTO;
import com.cinematchbackend.dto.response.SalaResponseDTO;
import com.cinematchbackend.services.interfaces.SalaService;
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
public class SalaGraphQLController {

    private final SalaService salaService;

    @QueryMapping
    public SalaResponseDTO obtenerSala(@Argument Long id) {
        return salaService.obtenerSala(id);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public SalaResponseDTO crearSala(@Argument SalaRequestDTO input) {
        log.info("Creando sala para usuario {}", input.getUsuarioId());
        return salaService.crearSala(input);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public SalaResponseDTO unirseASala(@Argument UnirseSalaDTO input) {
        log.info("Usuario {} uniéndose a sala con código {}", input.getUsuarioId(), input.getCodigo());
        return salaService.unirseASala(input);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean finalizarSala(@Argument Long id) {
        log.info("Finalizando sala {}", id);
        salaService.finalizarSala(id);
        return true;
    }
}
