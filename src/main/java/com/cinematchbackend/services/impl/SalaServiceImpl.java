package com.cinematchbackend.services.impl;

import com.cinematchbackend.dto.request.SalaRequestDTO;
import com.cinematchbackend.dto.request.UnirseSalaDTO;
import com.cinematchbackend.dto.response.MatchResponseDTO;
import com.cinematchbackend.dto.response.SalaResponseDTO;
import com.cinematchbackend.dto.response.UsuarioResponseDTO;
import com.cinematchbackend.entities.SalaEntidad;
import com.cinematchbackend.entities.UsuarioEntidad;
import com.cinematchbackend.enums.EstadoSala;
import com.cinematchbackend.exceptions.CodigoSalaInvalidoException;
import com.cinematchbackend.exceptions.SalaActivaException;
import com.cinematchbackend.exceptions.SalaNoEncontradaException;
import com.cinematchbackend.exceptions.UsuarioNoEncontradoException;
import com.cinematchbackend.repositories.MatchRepository;
import com.cinematchbackend.repositories.MensajeRepository;
import com.cinematchbackend.repositories.SalaRepository;
import com.cinematchbackend.repositories.UsuarioRepository;
import com.cinematchbackend.services.interfaces.SalaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalaServiceImpl implements SalaService {

    private final SalaRepository salaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MatchRepository matchRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MensajeRepository mensajeRepository;

    @Override
    public SalaResponseDTO crearSala(SalaRequestDTO dto) {
        UsuarioEntidad usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + dto.getUsuarioId()));

        SalaEntidad sala = new SalaEntidad.Builder()
                .codigo(generarCodigoUnico())
                .estado(EstadoSala.ESPERANDO)
                .usuario1(usuario)
                .build();

        log.info("Sala creada exitosamanete {}:",sala.getCodigo());
        return mapearAResponse(salaRepository.save(sala));
    }

    @Override
    public SalaResponseDTO unirseASala(UnirseSalaDTO dto) {
        SalaEntidad sala = salaRepository.findByCodigo(dto.getCodigo())
                .orElseThrow(() -> new CodigoSalaInvalidoException("Código de sala inválido: " + dto.getCodigo()));

        if (sala.getEstado() != EstadoSala.ESPERANDO) {
            throw new SalaActivaException("La sala ya está activa o finalizada");
        }

        UsuarioEntidad usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + dto.getUsuarioId()));

        sala.setUsuario2(usuario);
        sala.setEstado(EstadoSala.ACTIVA);

        return mapearAResponse(salaRepository.save(sala));
    }

    @Override
    public SalaResponseDTO obtenerSala(Long salaId) {
        SalaEntidad sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new SalaNoEncontradaException("Sala no encontrada: " + salaId));
        return mapearAResponse(sala);
    }

    @Override
    public void finalizarSala(Long salaId) {
        SalaEntidad sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new SalaNoEncontradaException("Sala no encontrada: " + salaId));
        sala.setEstado(EstadoSala.FINALIZADA);
        salaRepository.save(sala);

        messagingTemplate.convertAndSend(
                "/topic/sala/" + salaId + "/finalizar",
                true
        );

        mensajeRepository.deleteBySalaId(salaId); // ← aquí
    }

    @Override
    public void eliminarDatosSala(Long salaId) {
        SalaEntidad sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new SalaNoEncontradaException("Sala no encontrada: " + salaId));
        salaRepository.delete(sala);
    }

    @Override
    public List<MatchResponseDTO> obtenerMatches(Long salaId) {
        return matchRepository.findBySalaId(salaId).stream()
                .map(match -> {
                    MatchResponseDTO dto = new MatchResponseDTO();
                    dto.setId(match.getId());
                    dto.setFechaDeteccion(match.getFechaDeteccion());
                    return dto;
                }).toList();
    }

    private String generarCodigoUnico() {
        String codigo;
        do {
            codigo = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (salaRepository.findByCodigo(codigo).isPresent());
        return codigo;
    }

    private SalaResponseDTO mapearAResponse(SalaEntidad sala) {
        SalaResponseDTO response = new SalaResponseDTO();
        response.setId(sala.getId());
        response.setCodigo(sala.getCodigo());
        response.setEstado(sala.getEstado());
        response.setFechaCreacion(sala.getFechaCreacion());
        if (sala.getUsuario1() != null) response.setUsuario1(mapearUsuario(sala.getUsuario1()));
        if (sala.getUsuario2() != null) response.setUsuario2(mapearUsuario(sala.getUsuario2()));
        return response;
    }

    private UsuarioResponseDTO mapearUsuario(UsuarioEntidad usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setCorreo(usuario.getCorreo());
        dto.setDescripcion(usuario.getDescripcion());
        dto.setRol(usuario.getRol());
        return dto;
    }
}