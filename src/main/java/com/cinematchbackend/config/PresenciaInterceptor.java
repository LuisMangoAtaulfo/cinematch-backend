package com.cinematchbackend.config;

import com.cinematchbackend.dto.response.PresenciaDTO;
import com.cinematchbackend.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PresenciaInterceptor implements ChannelInterceptor {

    private final SimpMessagingTemplate messagingTemplate;
    private final UsuarioRepository usuarioRepository;

    public PresenciaInterceptor(@Lazy SimpMessagingTemplate messagingTemplate,
                                UsuarioRepository usuarioRepository) {
        this.messagingTemplate = messagingTemplate;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || accessor.getCommand() != StompCommand.SUBSCRIBE) {
            return message;
        }

        String destination = accessor.getDestination();
        if (destination == null || !destination.matches("/topic/sala/\\d+/.*")) {
            return message;
        }

        String[] parts = destination.split("/");
        if (parts.length < 4) return message;

        Long salaId;
        try {
            salaId = Long.parseLong(parts[3]);
        } catch (NumberFormatException e) {
            return message;
        }

        String usuarioIdHeader = accessor.getFirstNativeHeader("usuario-id");
        if (usuarioIdHeader == null) return message;

        Long usuarioId;
        try {
            usuarioId = Long.parseLong(usuarioIdHeader);
        } catch (NumberFormatException e) {
            return message;
        }

        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes == null) return message;

        sessionAttributes.put("salaId", salaId);
        sessionAttributes.put("usuarioId", usuarioId);

        usuarioRepository.findById(usuarioId).ifPresent(usuario -> {
            PresenciaDTO dto = new PresenciaDTO();
            dto.setUsuarioId(usuarioId);
            dto.setEstado("CONECTADO");
            dto.setNombreUsuario(usuario.getNombre());
            messagingTemplate.convertAndSend(
                    "/topic/sala/" + salaId + "/presencia", dto);
        });

        return message;
    }
}