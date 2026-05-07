package com.cinematchbackend.observer;

import com.cinematchbackend.dto.response.MatchResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioObserver implements MatchObserver {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void notificar(Long salaId, String contenidoId) {
        MatchResponseDTO notificacion = new MatchResponseDTO();
        notificacion.setId(salaId);
        messagingTemplate.convertAndSend("/topic/sala/" + salaId + "/match", notificacion);
    }
}