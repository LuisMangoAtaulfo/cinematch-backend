package com.cinematchbackend.services.impl;

import com.cinematchbackend.dto.response.CambioSalaDTO;
import com.cinematchbackend.dto.response.EstadoCompaneroDTO;
import com.cinematchbackend.dto.response.EvaluacionNotificationDTO;
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

    private final Sinks.Many<MensajeResponseDTO> mensajesSink =
            Sinks.many().multicast().onBackpressureBuffer(256, false);

    private final Sinks.Many<MatchResponseDTO> matchesSink =
            Sinks.many().multicast().onBackpressureBuffer(256, false);

    private final Sinks.Many<Boolean> salaFinalizadaSink =
            Sinks.many().multicast().onBackpressureBuffer(256, false);

    private final Sinks.Many<EstadoCompaneroDTO> estadoCompaneroSink =
            Sinks.many().multicast().onBackpressureBuffer(256, false);

    private final Sinks.Many<EvaluacionNotificationDTO> evaluacionesSink =
            Sinks.many().multicast().onBackpressureBuffer(256, false);

    private final Sinks.Many<CambioSalaDTO> cambiosSalaSink =
            Sinks.many().multicast().onBackpressureBuffer(256, false);

    @Override
    public void publicarMensaje(Long salaId, MensajeResponseDTO mensaje) {
        emit(mensajesSink.tryEmitNext(mensaje), "mensaje", salaId);
    }

    @Override
    public void publicarMatch(Long salaId, MatchResponseDTO match) {
        emit(matchesSink.tryEmitNext(match), "match", salaId);
    }

    @Override
    public void publicarSalaFinalizada(Long salaId) {
        emit(salaFinalizadaSink.tryEmitNext(true), "salaFinalizada", salaId);
    }

    @Override
    public void publicarEstadoCompanero(Long salaId, EstadoCompaneroDTO estado) {
        emit(estadoCompaneroSink.tryEmitNext(estado), "estadoCompanero", salaId);
    }

    @Override
    public void publicarEvaluacion(Long salaId, EvaluacionNotificationDTO evaluacion) {
        emit(evaluacionesSink.tryEmitNext(evaluacion), "evaluacion", salaId);
    }

    @Override
    public void publicarCambioSala(Long salaId, CambioSalaDTO cambio) {
        emit(cambiosSalaSink.tryEmitNext(cambio), "cambioSala", salaId);
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

    @Override
    public Flux<EstadoCompaneroDTO> suscribirseEstadoCompanero(Long salaId) {
        return estadoCompaneroSink.asFlux()
                .doOnSubscribe(s -> log.debug("Suscrito a estado compañero de sala {}", salaId));
    }

    @Override
    public Flux<EvaluacionNotificationDTO> suscribirseEvaluaciones(Long salaId) {
        return evaluacionesSink.asFlux()
                .filter(e -> salaId.equals(e.getSalaId()))
                .doOnSubscribe(s -> log.debug("Suscrito a evaluaciones de sala {}", salaId));
    }

    @Override
    public Flux<CambioSalaDTO> suscribirseCambiosSala(Long salaId) {
        return cambiosSalaSink.asFlux()
                .filter(c -> salaId.equals(c.getSalaId()))
                .doOnSubscribe(s -> log.debug("Suscrito a cambios de sala {}", salaId));
    }

    private void emit(Sinks.EmitResult result, String tipo, Long salaId) {
        if (result.isFailure()) {
            log.warn("Error al publicar {} para sala {}: {}", tipo, salaId, result);
        }
    }
}
