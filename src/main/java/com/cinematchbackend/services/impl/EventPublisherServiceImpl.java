package com.cinematchbackend.services.impl;

import com.cinematchbackend.dto.response.MatchResponseDTO;
import com.cinematchbackend.dto.response.MensajeResponseDTO;
import com.cinematchbackend.services.interfaces.EventPublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
@Slf4j
public class EventPublisherServiceImpl implements EventPublisherService {

    // autoCancel=false: sin esto, cuando el último suscriptor se desconecta el
    // sink queda completado permanentemente y todas las suscripciones futuras
    // reciben un Flux vacío (los eventos en vivo se perdían tras el primer
    // navigate/refresh). Los eventos emitidos sin suscriptores quedan en el
    // buffer de 256 y se entregan al siguiente suscriptor.
    private final Sinks.Many<MensajeResponseDTO> mensajesSink =
            Sinks.many().multicast().onBackpressureBuffer(256, false);

    private final Sinks.Many<MatchResponseDTO> matchesSink =
            Sinks.many().multicast().onBackpressureBuffer(256, false);

    private final Sinks.Many<Boolean> salaFinalizadaSink =
            Sinks.many().multicast().onBackpressureBuffer(256, false);

    @Override
    public void publicarMensaje(Long salaId, MensajeResponseDTO mensaje) {
        Sinks.EmitResult result = mensajesSink.tryEmitNext(mensaje);
        if (result.isFailure()) {
            log.warn("Error al publicar mensaje para sala {}: {}", salaId, result);
        }
    }

    @Override
    public void publicarMatch(Long salaId, MatchResponseDTO match) {
        Sinks.EmitResult result = matchesSink.tryEmitNext(match);
        if (result.isFailure()) {
            log.warn("Error al publicar match para sala {}: {}", salaId, result);
        }
    }

    @Override
    public void publicarSalaFinalizada(Long salaId) {
        Sinks.EmitResult result = salaFinalizadaSink.tryEmitNext(true);
        if (result.isFailure()) {
            log.warn("Error al publicar sala finalizada para sala {}: {}", salaId, result);
        }
    }

    @Override
    public Flux<MensajeResponseDTO> suscribirseMensajes(Long salaId) {
        return mensajesSink.asFlux()
                .filter(m -> salaId.equals(m.getSalaId()))
                .doOnSubscribe(s -> log.debug("Suscrito a mensajes de sala {}", salaId));
    }

    @Override
    public Flux<MatchResponseDTO> suscribirseMatches(Long salaId) {
        return matchesSink.asFlux()
                .filter(m -> salaId.equals(m.getSalaId()))
                .doOnSubscribe(s -> log.debug("Suscrito a matches de sala {}", salaId));
    }

    @Override
    public Flux<Boolean> suscribirseSalaFinalizada(Long salaId) {
        return salaFinalizadaSink.asFlux()
                .doOnSubscribe(s -> log.debug("Suscrito a sala finalizada {}", salaId));
    }
}
