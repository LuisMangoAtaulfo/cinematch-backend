package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.response.MensajeResponseDTO;
import com.cinematchbackend.services.interfaces.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatGraphQLController {

    private final ChatService chatService;

    @QueryMapping
    public List<MensajeResponseDTO> historialChat(@Argument Long salaId) {
        return chatService.obtenerMensajes(salaId);
    }
}
