package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.response.EstadoCompaneroDTO;
import com.cinematchbackend.dto.response.EvaluacionNotificationDTO;
import com.cinematchbackend.dto.response.MatchResponseDTO;
import com.cinematchbackend.dto.response.MensajeResponseDTO;
import com.cinematchbackend.services.interfaces.EventPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SubscriptionGraphQLController {

    private final EventPublisherService eventPublisherService;

    @SubscriptionMapping
    @PreAuthorize("isAuthenticated()")
    public Flux<MensajeResponseDTO> mensajeRecibido(@Argument Long salaId) {
        log.info("Nueva suscripción a mensajes de sala {}", salaId);
        return eventPublisherService.suscribirseMensajes(salaId);
    }

    @SubscriptionMapping
    @PreAuthorize("isAuthenticated()")
    public Flux<MatchResponseDTO> matchDetectado(@Argument Long salaId) {
        log.info("Nueva suscripción a matches de sala {}", salaId);
        return eventPublisherService.suscribirseMatches(salaId);
    }

    @SubscriptionMapping
    @PreAuthorize("isAuthenticated()")
    public Flux<Boolean> salaFinalizada(@Argument Long salaId) {
        log.info("Nueva suscripción a sala finalizada {}", salaId);
        return eventPublisherService.suscribirseSalaFinalizada(salaId);
    }

    @SubscriptionMapping
    @PreAuthorize("isAuthenticated()")
    public Flux<EstadoCompaneroDTO> estadoCompanero(@Argument Long salaId) {
        log.info("Nueva suscripción a estado compañero de sala {}", salaId);
        return eventPublisherService.suscribirseEstadoCompanero(salaId);
    }

    @SubscriptionMapping
    @PreAuthorize("isAuthenticated()")
    public Flux<EvaluacionNotificationDTO> evaluacionRecibida(@Argument Long salaId) {
        log.info("Nueva suscripción a evaluaciones de sala {}", salaId);
        return eventPublisherService.suscribirseEvaluaciones(salaId);
    }
}
