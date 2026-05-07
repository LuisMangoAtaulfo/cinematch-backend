package com.cinematchbackend.services.impl;

import com.cinematchbackend.dto.response.ContenidoResponseDTO;
import com.cinematchbackend.dto.response.MatchResponseDTO;
import com.cinematchbackend.entities.ContenidoEntidad;
import com.cinematchbackend.entities.MatchEntidad;
import com.cinematchbackend.entities.SalaEntidad;
import com.cinematchbackend.exceptions.ContenidoNoEncontradoException;
import com.cinematchbackend.exceptions.SalaNoEncontradaException;
import com.cinematchbackend.repositories.ContenidoRepository;
import com.cinematchbackend.repositories.MatchRepository;
import com.cinematchbackend.repositories.SalaRepository;
import com.cinematchbackend.services.interfaces.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final SalaRepository salaRepository;
    private final ContenidoRepository contenidoRepository;

    @Override
    public List<MatchResponseDTO> buscarMatches(Long salaId) {
        return matchRepository.findBySalaId(salaId).stream()
                .map(this::mapearAResponse)
                .toList();
    }

    @Override
    public MatchEntidad registrarMatch(Long salaId, String contenidoId) {
        SalaEntidad sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new SalaNoEncontradaException("Sala no encontrada: " + salaId));
        ContenidoEntidad contenido = contenidoRepository.findById(contenidoId)
                .orElseThrow(() -> new ContenidoNoEncontradoException("Contenido no encontrado: " + contenidoId));
        MatchEntidad match = new MatchEntidad();
        match.setSala(sala);
        match.setContenido(contenido);
        return matchRepository.save(match);
    }

    private MatchResponseDTO mapearAResponse(MatchEntidad match) {
        MatchResponseDTO dto = new MatchResponseDTO();
        dto.setId(match.getId());
        dto.setFechaDeteccion(match.getFechaDeteccion());
        dto.setContenido(mapearContenido(match.getContenido()));
        return dto;
    }

    private ContenidoResponseDTO mapearContenido(ContenidoEntidad contenido) {
        ContenidoResponseDTO dto = new ContenidoResponseDTO();
        dto.setContenidoId(contenido.getContenidoId());
        dto.setTitulo(contenido.getTitulo());
        dto.setAnio(contenido.getAnio());
        dto.setTipo(contenido.getTipo());
        dto.setGenero(contenido.getGenero());
        dto.setImagen(contenido.getImagen());
        return dto;
    }
}