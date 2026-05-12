package com.cinematchbackend.config;

import com.cinematchbackend.dto.response.PresenciaDTO;
import com.cinematchbackend.repositories.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@Component
@Slf4j
public class PresenciaEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final UsuarioRepository usuarioRepository;

    public PresenciaEventListener(@Lazy SimpMessagingTemplate messagingTemplate,
                                  UsuarioRepository usuarioRepository) {
        this.messagingTemplate = messagingTemplate;
        this.usuarioRepository = usuarioRepository;
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        if (sessionAttributes == null) return;

        Long salaId = (Long) sessionAttributes.get("salaId");
        Long usuarioId = (Long) sessionAttributes.get("usuarioId");

        if (salaId == null || usuarioId == null) return;

        usuarioRepository.findById(usuarioId).ifPresent(usuario -> {
            PresenciaDTO dto = new PresenciaDTO();
            dto.setUsuarioId(usuarioId);
            dto.setEstado("DESCONECTADO");
            dto.setNombreUsuario(usuario.getNombre());
            messagingTemplate.convertAndSend(
                    "/topic/sala/" + salaId + "/presencia", dto);
            log.info("Usuario {} desconectado de sala {}", usuarioId, salaId);
        });
    }
}