package com.cinematchbackend.services.impl;

import com.cinematchbackend.dto.request.FiltroRequestDTO;
import com.cinematchbackend.dto.response.ContenidoResponseDTO;
import com.cinematchbackend.entities.ContenidoEntidad;
import com.cinematchbackend.entities.FiltroEntidad;
import com.cinematchbackend.entities.SalaEntidad;
import com.cinematchbackend.exceptions.SalaNoEncontradaException;
import com.cinematchbackend.repositories.FiltroRepository;
import com.cinematchbackend.repositories.SalaRepository;
import com.cinematchbackend.services.interfaces.FiltroService;
import com.cinematchbackend.strategy.FiltroContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FiltroServiceImpl implements FiltroService {

    private final FiltroRepository filtroRepository;
    private final SalaRepository salaRepository;
    private final FiltroContext filtroContext;

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
        filtro.setAnio(dto.getAnio()); // ← NUEVO

        filtroRepository.save(filtro);

        return filtroContext.ejecutar(dto).stream()
                .map(this::mapearAResponse)
                .toList();
    }

    @Override
    public Optional<List<ContenidoResponseDTO>> obtenerContenidoPorSala(Long salaId) {
        return filtroRepository.findBySalaId(salaId).map(filtro -> {
            FiltroRequestDTO dto = new FiltroRequestDTO();
            dto.setSalaId(salaId);
            dto.setTipo(filtro.getTipo());
            dto.setGenero(filtro.getGenero());
            dto.setPlataforma(filtro.getPlataforma());
            dto.setAnio(filtro.getAnio()); // ← NUEVO

            return filtroContext.ejecutar(dto).stream()
                    .map(this::mapearAResponse)
                    .toList();
        });
    }

    private ContenidoResponseDTO mapearAResponse(ContenidoEntidad contenido) {
        ContenidoResponseDTO dto = new ContenidoResponseDTO();
        dto.setContenidoId(contenido.getContenidoId());
        dto.setTitulo(contenido.getTitulo());
        dto.setAnio(contenido.getAnio());
        dto.setTipo(contenido.getTipo());
        dto.setGenero(contenido.getGenero());
        dto.setImagen(contenido.getImagen());
        dto.setPlataformas(
                contenido.getPlataformas().stream()
                        .map(cp -> cp.getPlataforma().getNombre())
                        .toList()
        );
        return dto;
    }
}