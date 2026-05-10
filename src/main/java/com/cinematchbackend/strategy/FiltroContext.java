package com.cinematchbackend.strategy;

import com.cinematchbackend.dto.request.FiltroRequestDTO;
import com.cinematchbackend.entities.ContenidoEntidad;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FiltroContext {

    private final List<FiltroStrategy> estrategias;

    public List<ContenidoEntidad> ejecutar(FiltroRequestDTO dto) {
        return estrategias.stream()
                .filter(e -> e.aplica(dto))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No se encontró estrategia de filtrado"))
                .filtrar(dto);
    }
}