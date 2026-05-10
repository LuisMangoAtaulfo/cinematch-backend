package com.cinematchbackend.strategy;

import com.cinematchbackend.dto.request.FiltroRequestDTO;
import com.cinematchbackend.entities.ContenidoEntidad;

import java.util.List;

public interface FiltroStrategy {
    List<ContenidoEntidad> filtrar(FiltroRequestDTO dto);
    boolean aplica(FiltroRequestDTO dto);
}