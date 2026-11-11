package com.cinematchbackend.services.interfaces;

import com.cinematchbackend.dto.request.SalaRequestDTO;
import com.cinematchbackend.dto.request.UnirseSalaDTO;
import com.cinematchbackend.dto.response.EstadoSalaCompletaDTO;
import com.cinematchbackend.dto.response.MatchResponseDTO;
import com.cinematchbackend.dto.response.ResultadoSalaDTO;
import com.cinematchbackend.dto.response.SalaResponseDTO;

import java.util.List;

public interface SalaService {
    SalaResponseDTO crearSala(SalaRequestDTO dto);
    SalaResponseDTO unirseASala(UnirseSalaDTO dto);
    SalaResponseDTO obtenerSala(Long salaId);
    SalaResponseDTO buscarSalaPorCodigo(String codigo);
    void finalizarSala(Long salaId, Long usuarioId);
    void cancelarSala(Long salaId, Long usuarioId);
    void eliminarDatosSala(Long salaId);
    List<MatchResponseDTO> obtenerMatches(Long salaId);
    ResultadoSalaDTO obtenerResultados(Long salaId);
    EstadoSalaCompletaDTO obtenerEstadoSala(Long salaId);
}
