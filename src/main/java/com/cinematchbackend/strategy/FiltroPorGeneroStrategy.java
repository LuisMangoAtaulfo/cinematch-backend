package com.cinematchbackend.strategy;

import com.cinematchbackend.dto.request.FiltroRequestDTO;
import com.cinematchbackend.entities.ContenidoEntidad;
import com.cinematchbackend.repositories.ContenidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FiltroPorGeneroStrategy implements FiltroStrategy {

    private final ContenidoRepository contenidoRepository;

    @Override
    public boolean aplica(FiltroRequestDTO dto) {
        return dto.getPlataforma() == null && dto.getTipo() == null && dto.getGenero() != null;
    }

    @Override
    public List<ContenidoEntidad> filtrar(FiltroRequestDTO dto) {
        return contenidoRepository.findByGenero(dto.getGenero());
    }
}