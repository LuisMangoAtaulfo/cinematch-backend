package com.cinematchbackend.services.interfaces;

import com.cinematchbackend.dto.request.FiltroRequestDTO;
import com.cinematchbackend.dto.response.ContenidoResponseDTO;
import com.cinematchbackend.dto.response.FiltroResultadoDTO;

import java.util.List;
import java.util.Optional;

public interface FiltroService {
    List<ContenidoResponseDTO> procesarFiltros(FiltroRequestDTO dto);
    FiltroResultadoDTO procesarFiltrosConTotal(FiltroRequestDTO dto);
    FiltroResultadoDTO filtradoPublico(FiltroRequestDTO dto);
    Optional<List<ContenidoResponseDTO>> obtenerContenidoPorSala(Long salaId);
    List<String> obtenerGenerosDisponibles();
}
