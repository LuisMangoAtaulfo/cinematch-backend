package com.cinematchbackend.services.interfaces;

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

    Flux<MensajeResponseDTO> suscribirseMensajes(Long salaId);

    Flux<MatchResponseDTO> suscribirseMatches(Long salaId);

    Flux<Boolean> suscribirseSalaFinalizada(Long salaId);
}
