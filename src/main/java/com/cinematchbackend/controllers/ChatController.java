package com.cinematchbackend.controllers;

import com.cinematchbackend.dto.request.MensajeRequestDTO;
import com.cinematchbackend.dto.response.MensajeResponseDTO;
import com.cinematchbackend.services.interfaces.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    // En lugar de @SendTo, inyectar SimpMessagingTemplate
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.enviar")
    public void enviarMensaje(MensajeRequestDTO dto) {
        MensajeResponseDTO response = chatService.procesarMensaje(dto);
        messagingTemplate.convertAndSend("/topic/sala/" + dto.getSalaId(), response);
    }

    @GetMapping("/{salaId}")
    public List<MensajeResponseDTO> obtenerMensajes(@PathVariable Long salaId) {
        return chatService.obtenerMensajes(salaId);
    }
}