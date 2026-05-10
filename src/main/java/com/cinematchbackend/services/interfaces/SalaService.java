package com.cinematchbackend.services.interfaces;

import com.cinematchbackend.dto.request.SalaRequestDTO;
import com.cinematchbackend.dto.request.UnirseSalaDTO;
import com.cinematchbackend.dto.response.MatchResponseDTO;
import com.cinematchbackend.dto.response.SalaResponseDTO;

import java.util.List;

public interface SalaService {
    SalaResponseDTO crearSala(SalaRequestDTO dto);
    SalaResponseDTO unirseASala(UnirseSalaDTO dto);
    SalaResponseDTO obtenerSala(Long salaId);
    void finalizarSala(Long salaId);
    void eliminarDatosSala(Long salaId);
    List<MatchResponseDTO> obtenerMatches(Long salaId);
}