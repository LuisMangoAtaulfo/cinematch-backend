package com.cinematchbackend.controllers;

import com.cinematchbackend.dto.request.MensajeRequestDTO;
import com.cinematchbackend.dto.response.MensajeResponseDTO;
import com.cinematchbackend.services.interfaces.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat.enviar")
    @SendTo("/topic/sala")
    public MensajeResponseDTO enviarMensaje(MensajeRequestDTO dto) {
        return chatService.procesarMensaje(dto);
    }

    @GetMapping("/{salaId}")
    public List<MensajeResponseDTO> obtenerMensajes(@PathVariable Long salaId) {
        return chatService.obtenerMensajes(salaId);
    }
}