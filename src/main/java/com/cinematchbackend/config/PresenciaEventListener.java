package com.cinematchbackend.config;

import com.cinematchbackend.dto.response.EstadoCompaneroDTO;
import com.cinematchbackend.repositories.UsuarioRepository;
import com.cinematchbackend.services.interfaces.ConexionService;
import com.cinematchbackend.services.interfaces.EventPublisherService;
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
    private final EventPublisherService eventPublisherService;
    private final ConexionService conexionService;

    public PresenciaEventListener(@Lazy SimpMessagingTemplate messagingTemplate,
                                   UsuarioRepository usuarioRepository,
                                   EventPublisherService eventPublisherService,
                                   ConexionService conexionService) {
        this.messagingTemplate = messagingTemplate;
        this.usuarioRepository = usuarioRepository;
        this.eventPublisherService = eventPublisherService;
        this.conexionService = conexionService;
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        if (sessionAttributes == null) return;

        Long salaId = (Long) sessionAttributes.get("salaId");
        Long usuarioId = (Long) sessionAttributes.get("usuarioId");

        if (salaId == null || usuarioId == null) return;

        // Registrar desconexion en el servicio de conexión
        conexionService.registrarDesconexion(salaId, usuarioId);

        usuarioRepository.findById(usuarioId).ifPresent(usuario -> {
            EstadoCompaneroDTO dto = new EstadoCompaneroDTO();
            dto.setUsuarioId(usuarioId);
            dto.setEstado("DESCONECTADO");
            dto.setNombreUsuario(usuario.getNombre());

            // STOMP WebSocket (legacy)
            messagingTemplate.convertAndSend(
                    "/topic/sala/" + salaId + "/presencia", dto);

            // GraphQL Subscription (R-BE-03)
            eventPublisherService.publicarEstadoCompanero(salaId, dto);

            log.info("Usuario {} desconectado de sala {}", usuarioId, salaId);
        });
    }
}
