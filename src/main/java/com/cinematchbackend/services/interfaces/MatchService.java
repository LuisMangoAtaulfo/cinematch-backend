package com.cinematchbackend.services.interfaces;

import com.cinematchbackend.dto.response.MatchResponseDTO;
import com.cinematchbackend.entities.MatchEntidad;

import java.util.List;

public interface MatchService {
    List<MatchResponseDTO> buscarMatches(Long salaId);
    MatchEntidad registrarMatch(Long salaId, String contenidoId);
}