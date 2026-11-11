package com.cinematchbackend.services.interfaces;

import com.cinematchbackend.dto.response.CambioSalaDTO;
import com.cinematchbackend.dto.response.EstadoCompaneroDTO;
import com.cinematchbackend.dto.response.EvaluacionNotificationDTO;
import com.cinematchbackend.dto.response.MatchResponseDTO;
import com.cinematchbackend.dto.response.MensajeResponseDTO;
import reactor.core.publisher.Flux;

/**
 * Servicio centralizado para publicar y suscribirse a eventos en tiempo real.
 * Usa Reactor Sinks para emitir eventos a los suscriptores GraphQL.
 */
public interface EventPublisherService {

    void publicarMensaje(Long salaId, MensajeResponseDTO mensaje);

    void publicarMatch(Long salaId, MatchResponseDTO match);

    void publicarSalaFinalizada(Long salaId);

    void publicarEstadoCompanero(Long salaId, EstadoCompaneroDTO estado);

    void publicarEvaluacion(Long salaId, EvaluacionNotificationDTO evaluacion);

    void publicarCambioSala(Long salaId, CambioSalaDTO cambio);

    Flux<MensajeResponseDTO> suscribirseMensajes(Long salaId);

    Flux<MatchResponseDTO> suscribirseMatches(Long salaId);

    Flux<Boolean> suscribirseSalaFinalizada(Long salaId);

    Flux<EstadoCompaneroDTO> suscribirseEstadoCompanero(Long salaId);

    Flux<EvaluacionNotificationDTO> suscribirseEvaluaciones(Long salaId);

    Flux<CambioSalaDTO> suscribirseCambiosSala(Long salaId);
}
