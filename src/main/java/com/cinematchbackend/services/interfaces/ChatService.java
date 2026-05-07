package com.cinematchbackend.services.interfaces;

import com.cinematchbackend.dto.request.MensajeRequestDTO;
import com.cinematchbackend.dto.response.MensajeResponseDTO;

import java.util.List;

public interface ChatService {
    MensajeResponseDTO procesarMensaje(MensajeRequestDTO dto);
    List<MensajeResponseDTO> obtenerMensajes(Long salaId);
}