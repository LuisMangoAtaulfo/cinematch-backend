package com.cinematchbackend.strategy;

import com.cinematchbackend.dto.request.FiltroRequestDTO;
import com.cinematchbackend.entities.ContenidoEntidad;
import com.cinematchbackend.repositories.ContenidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FiltroPorPlataformaStrategy implements FiltroStrategy {

    private final ContenidoRepository contenidoRepository;

    @Override
    public boolean aplica(FiltroRequestDTO dto) {
        return dto.getPlataforma() != null;
    }

    @Override
    public List<ContenidoEntidad> filtrar(FiltroRequestDTO dto) {
        if (dto.getTipo() != null && dto.getGenero() != null) {
            return contenidoRepository.findByPlataformaNombreAndTipoAndGenero(
                    dto.getPlataforma(), dto.getTipo(), dto.getGenero());
        }
        if (dto.getTipo() != null) {
            return contenidoRepository.findByPlataformaNombreAndTipo(
                    dto.getPlataforma(), dto.getTipo());
        }
        if (dto.getGenero() != null) {
            return contenidoRepository.findByPlataformaNombreAndGenero(
                    dto.getPlataforma(), dto.getGenero());
        }
        return contenidoRepository.findByPlataformaNombre(dto.getPlataforma());
    }
}