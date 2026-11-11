package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.request.SalaRequestDTO;
import com.cinematchbackend.dto.request.UnirseSalaDTO;
import com.cinematchbackend.dto.response.CambioSalaDTO;
import com.cinematchbackend.dto.response.EstadoConexionDTO;
import com.cinematchbackend.dto.response.EstadoSalaCompletaDTO;
import com.cinematchbackend.dto.response.SalaResponseDTO;
import com.cinematchbackend.services.interfaces.EventPublisherService;
import com.cinematchbackend.services.interfaces.SalaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SalaGraphQLController {

    private final SalaService salaService;
    private final EventPublisherService eventPublisherService;

    @QueryMapping
    public SalaResponseDTO obtenerSala(@Argument Long id) {
        return salaService.obtenerSala(id);
    }

    @QueryMapping
    public SalaResponseDTO buscarSalaPorCodigo(@Argument String codigo) {
        return salaService.buscarSalaPorCodigo(codigo);
    }

    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public EstadoSalaCompletaDTO obtenerEstadoSala(@Argument Long salaId) {
        return salaService.obtenerEstadoSala(salaId);
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
    public Boolean finalizarSala(@Argument Long id, @Argument Long usuarioId) {
        log.info("Finalizando sala {} por usuario {}", id, usuarioId);
        salaService.finalizarSala(id, usuarioId);
        return true;
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean cancelarSala(@Argument Long id, @Argument Long usuarioId) {
        log.info("Cancelando sala {} por usuario {}", id, usuarioId);
        salaService.cancelarSala(id, usuarioId);
        return true;
    }

    @SubscriptionMapping
    @PreAuthorize("isAuthenticated()")
    public Flux<CambioSalaDTO> cambiosSala(@Argument Long salaId) {
        log.info("Nueva suscripción a cambios de sala {}", salaId);
        return eventPublisherService.suscribirseCambiosSala(salaId);
    }
}
