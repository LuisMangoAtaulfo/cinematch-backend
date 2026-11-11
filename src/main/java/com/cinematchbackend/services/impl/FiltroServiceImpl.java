package com.cinematchbackend.services.impl;

import com.cinematchbackend.dto.request.FiltroRequestDTO;
import com.cinematchbackend.dto.response.ContenidoResponseDTO;
import com.cinematchbackend.dto.response.FiltroResultadoDTO;
import com.cinematchbackend.entities.ContenidoEntidad;
import com.cinematchbackend.entities.FiltroEntidad;
import com.cinematchbackend.entities.SalaEntidad;
import com.cinematchbackend.enums.GeneroContenido;
import com.cinematchbackend.exceptions.SalaNoEncontradaException;
import com.cinematchbackend.repositories.ContenidoRepository;
import com.cinematchbackend.repositories.FiltroRepository;
import com.cinematchbackend.repositories.SalaRepository;
import com.cinematchbackend.services.interfaces.FiltroService;
import com.cinematchbackend.strategy.FiltroContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FiltroServiceImpl implements FiltroService {

    private final FiltroRepository filtroRepository;
    private final SalaRepository salaRepository;
    private final ContenidoRepository contenidoRepository;
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
        filtro.setAnio(dto.getAnio());

        filtroRepository.save(filtro);

        return filtroContext.ejecutar(dto).stream()
                .map(this::mapearAResponse)
                .toList();
    }

    @Override
    public FiltroResultadoDTO procesarFiltrosConTotal(FiltroRequestDTO dto) {
        List<ContenidoResponseDTO> contenidos = procesarFiltros(dto);
        FiltroResultadoDTO resultado = new FiltroResultadoDTO();
        resultado.setContenidos(contenidos);
        resultado.setTotalResultados(contenidos.size());
        return resultado;
    }

    @Override
    public FiltroResultadoDTO filtradoPublico(FiltroRequestDTO dto) {
        List<ContenidoEntidad> contenidos = filtroContext.ejecutar(dto);

        // Aplicar ordenamiento
        if ("anio".equalsIgnoreCase(dto.getOrderBy())) {
            contenidos = contenidos.stream()
                    .sorted(Comparator.comparing(ContenidoEntidad::getAnio).reversed())
                    .collect(Collectors.toList());
        }

        // Aplicar paginación
        int offset = dto.getOffset() != null ? dto.getOffset() : 0;
        int limit = dto.getLimit() != null ? dto.getLimit() : contenidos.size();

        List<ContenidoResponseDTO> paginados = contenidos.stream()
                .skip(offset)
                .limit(limit)
                .map(this::mapearAResponse)
                .toList();

        FiltroResultadoDTO resultado = new FiltroResultadoDTO();
        resultado.setContenidos(paginados);
        resultado.setTotalResultados(contenidos.size());
        return resultado;
    }

    @Override
    public Optional<List<ContenidoResponseDTO>> obtenerContenidoPorSala(Long salaId) {
        return filtroRepository.findBySalaId(salaId).map(filtro -> {
            FiltroRequestDTO dto = new FiltroRequestDTO();
            dto.setSalaId(salaId);
            dto.setTipo(filtro.getTipo());
            dto.setGenero(filtro.getGenero());
            dto.setPlataforma(filtro.getPlataforma());
            dto.setAnio(filtro.getAnio());

            return filtroContext.ejecutar(dto).stream()
                    .map(this::mapearAResponse)
                    .toList();
        });
    }

    @Override
    public List<String> obtenerGenerosDisponibles() {
        return contenidoRepository.findAll().stream()
                .map(c -> c.getGenero() != null ? c.getGenero().name() : null)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
    }

    private ContenidoResponseDTO mapearAResponse(ContenidoEntidad contenido) {
        ContenidoResponseDTO dto = new ContenidoResponseDTO();
        dto.setContenidoId(contenido.getContenidoId());
        dto.setTitulo(contenido.getTitulo());
        dto.setAnio(contenido.getAnio());
        dto.setTipo(contenido.getTipo());
        dto.setGenero(contenido.getGenero());
        dto.setImagen(contenido.getImagen());
        dto.setSinopsis(contenido.getSinopsis());
        dto.setPlataformas(
                contenido.getPlataformas().stream()
                        .map(cp -> cp.getPlataforma().getNombre())
                        .toList()
        );
        return dto;
    }
}
