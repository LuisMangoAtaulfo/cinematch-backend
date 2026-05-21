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
        boolean tieneAnio   = dto.getAnio() != null;
        boolean tieneTipo   = dto.getTipo() != null;
        boolean tieneGenero = dto.getGenero() != null;

        // ── Con año ──────────────────────────────────────────────────────────
        if (tieneAnio) {
            if (tieneTipo && tieneGenero) {
                return contenidoRepository.findByPlataformaNombreAndAnioAndTipoAndGenero(
                        dto.getPlataforma(), dto.getAnio(), dto.getTipo(), dto.getGenero());
            }
            if (tieneTipo) {
                return contenidoRepository.findByPlataformaNombreAndAnioAndTipo(
                        dto.getPlataforma(), dto.getAnio(), dto.getTipo());
            }
            if (tieneGenero) {
                return contenidoRepository.findByPlataformaNombreAndAnioAndGenero(
                        dto.getPlataforma(), dto.getAnio(), dto.getGenero());
            }
            return contenidoRepository.findByPlataformaNombreAndAnio(
                    dto.getPlataforma(), dto.getAnio());
        }

        if (tieneTipo && tieneGenero) {
            return contenidoRepository.findByPlataformaNombreAndTipoAndGenero(
                    dto.getPlataforma(), dto.getTipo(), dto.getGenero());
        }
        if (tieneTipo) {
            return contenidoRepository.findByPlataformaNombreAndTipo(
                    dto.getPlataforma(), dto.getTipo());
        }
        if (tieneGenero) {
            return contenidoRepository.findByPlataformaNombreAndGenero(
                    dto.getPlataforma(), dto.getGenero());
        }
        return contenidoRepository.findByPlataformaNombre(dto.getPlataforma());
    }
}