package com.cinematchbackend.observer;

import com.cinematchbackend.dto.response.MatchResponseDTO;
import com.cinematchbackend.services.interfaces.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioObserver implements MatchObserver {

    private final SimpMessagingTemplate messagingTemplate;
    private final MatchService matchService;

    @Override
    public void notificar(Long salaId, String contenidoId) {
        matchService.buscarMatches(salaId).stream()
                .filter(m -> m.getContenido().getContenidoId().equals(contenidoId))
                .findFirst()
                .ifPresent(match ->
                        messagingTemplate.convertAndSend(
                                "/topic/sala/" + salaId + "/match",
                                match
                        )
                );
    }
}