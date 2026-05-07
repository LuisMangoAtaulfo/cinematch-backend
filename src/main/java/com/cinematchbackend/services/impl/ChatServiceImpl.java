package com.cinematchbackend.services.impl;

import com.cinematchbackend.dto.request.MensajeRequestDTO;
import com.cinematchbackend.dto.response.MensajeResponseDTO;
import com.cinematchbackend.entities.MensajeEntidad;
import com.cinematchbackend.entities.SalaEntidad;
import com.cinematchbackend.entities.UsuarioEntidad;
import com.cinematchbackend.exceptions.SalaNoEncontradaException;
import com.cinematchbackend.exceptions.UsuarioNoEncontradoException;
import com.cinematchbackend.repositories.MensajeRepository;
import com.cinematchbackend.repositories.SalaRepository;
import com.cinematchbackend.repositories.UsuarioRepository;
import com.cinematchbackend.services.interfaces.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final MensajeRepository mensajeRepository;
    private final SalaRepository salaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public MensajeResponseDTO procesarMensaje(MensajeRequestDTO dto) {
        SalaEntidad sala = salaRepository.findById(dto.getSalaId())
                .orElseThrow(() -> new SalaNoEncontradaException("Sala no encontrada: " + dto.getSalaId()));
        UsuarioEntidad usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + dto.getUsuarioId()));
        MensajeEntidad mensaje = new MensajeEntidad();
        mensaje.setSala(sala);
        mensaje.setUsuario(usuario);
        mensaje.setTexto(dto.getTexto());
        return mapearAResponse(mensajeRepository.save(mensaje));
    }

    @Override
    public List<MensajeResponseDTO> obtenerMensajes(Long salaId) {
        return mensajeRepository.findBySalaIdOrderByFechaEnvioAsc(salaId).stream()
                .map(this::mapearAResponse)
                .toList();
    }

    private MensajeResponseDTO mapearAResponse(MensajeEntidad mensaje) {
        MensajeResponseDTO dto = new MensajeResponseDTO();
        dto.setId(mensaje.getId());
        dto.setTexto(mensaje.getTexto());
        dto.setFechaEnvio(mensaje.getFechaEnvio());
        dto.setUsuarioId(mensaje.getUsuario().getId());
        dto.setNombreUsuario(mensaje.getUsuario().getNombre());
        return dto;
    }
}