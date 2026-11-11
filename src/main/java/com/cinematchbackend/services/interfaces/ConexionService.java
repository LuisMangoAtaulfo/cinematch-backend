package com.cinematchbackend.services.interfaces;

import com.cinematchbackend.dto.response.EstadoConexionDTO;

import java.util.List;

/**
 * Gestiona el estado de conexión de los usuarios en una sala (R-BE-10).
 * Detecta desconexiones vía heartbeat de WebSocket y permite consultar
 * el estado actual de los participantes.
 */
public interface ConexionService {
    void registrarConexion(Long salaId, Long usuarioId);
    void registrarDesconexion(Long salaId, Long usuarioId);
    boolean estaConectado(Long salaId, Long usuarioId);
    List<EstadoConexionDTO> obtenerEstados(Long salaId);
    void limpiarEstados(Long salaId);
}
