package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.request.MensajeRequestDTO;
import com.cinematchbackend.dto.response.MensajeResponseDTO;
import com.cinematchbackend.services.interfaces.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatGraphQLController {

    private final ChatService chatService;

    @QueryMapping
    public List<MensajeResponseDTO> historialChat(@Argument Long salaId) {
        return chatService.obtenerMensajes(salaId);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public MensajeResponseDTO enviarMensaje(@Argument MensajeRequestDTO input) {
        log.info("Mensaje enviado en sala {} por usuario {}", input.getSalaId(), input.getUsuarioId());
        return chatService.procesarMensaje(input);
    }
}
