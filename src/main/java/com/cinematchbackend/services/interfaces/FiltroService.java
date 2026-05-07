package com.cinematchbackend.services.interfaces;

import com.cinematchbackend.dto.request.FiltroRequestDTO;
import com.cinematchbackend.dto.response.ContenidoResponseDTO;

import java.util.List;

public interface FiltroService {
    List<ContenidoResponseDTO> procesarFiltros(FiltroRequestDTO dto);
}