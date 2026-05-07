package com.cinematchbackend.services.impl;

import com.cinematchbackend.dto.request.FiltroRequestDTO;
import com.cinematchbackend.dto.response.ContenidoResponseDTO;
import com.cinematchbackend.entities.ContenidoEntidad;
import com.cinematchbackend.entities.FiltroEntidad;
import com.cinematchbackend.entities.SalaEntidad;
import com.cinematchbackend.exceptions.SalaNoEncontradaException;
import com.cinematchbackend.repositories.ContenidoRepository;
import com.cinematchbackend.repositories.FiltroRepository;
import com.cinematchbackend.repositories.SalaRepository;
import com.cinematchbackend.services.interfaces.FiltroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FiltroServiceImpl implements FiltroService {

    private final FiltroRepository filtroRepository;
    private final SalaRepository salaRepository;
    private final ContenidoRepository contenidoRepository;

    @Override
    public List<ContenidoResponseDTO> procesarFiltros(FiltroRequestDTO dto) {
        SalaEntidad sala = salaRepository.findById(dto.getSalaId())
                .orElseThrow(() -> new SalaNoEncontradaException("Sala no encontrada: " + dto.getSalaId()));

        FiltroEntidad filtro = filtroRepository.findBySalaId(dto.getSalaId())
                .orElse(new FiltroEntidad());
        filtro.setSala(sala);
        filtro.setTipo(dto.getTipo());
        filtro.setGenero(dto.getGenero());
        filtro.setPlataforma(dto.getPlataforma());
        filtroRepository.save(filtro);

        List<ContenidoEntidad> contenidos;
        if (dto.getTipo() != null && dto.getGenero() != null) {
            contenidos = contenidoRepository.findByTipoAndGenero(dto.getTipo(), dto.getGenero());
        } else if (dto.getTipo() != null) {
            contenidos = contenidoRepository.findByTipo(dto.getTipo());
        } else if (dto.getGenero() != null) {
            contenidos = contenidoRepository.findByGenero(dto.getGenero());
        } else {
            contenidos = contenidoRepository.findAll();
        }

        return contenidos.stream().map(this::mapearAResponse).toList();
    }

    private ContenidoResponseDTO mapearAResponse(ContenidoEntidad contenido) {
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