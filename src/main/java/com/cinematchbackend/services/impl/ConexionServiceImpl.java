package com.cinematchbackend.services.impl;

import com.cinematchbackend.dto.response.EstadoConexionDTO;
import com.cinematchbackend.entities.EstadoConexionEntidad;
import com.cinematchbackend.repositories.EstadoConexionRepository;
import com.cinematchbackend.repositories.UsuarioRepository;
import com.cinematchbackend.services.interfaces.ConexionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConexionServiceImpl implements ConexionService {

    private final EstadoConexionRepository estadoConexionRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public void registrarConexion(Long salaId, Long usuarioId) {
        EstadoConexionEntidad estado = estadoConexionRepository
                .findBySalaIdAndUsuarioId(salaId, usuarioId)
                .orElse(new EstadoConexionEntidad());
        estado.setSalaId(salaId);
        estado.setUsuarioId(usuarioId);
        estado.setConectado(true);
        estado.setUltimaActividad(LocalDateTime.now());
        estadoConexionRepository.save(estado);
    }

    @Override
    @Transactional
    public void registrarDesconexion(Long salaId, Long usuarioId) {
        estadoConexionRepository.findBySalaIdAndUsuarioId(salaId, usuarioId)
                .ifPresent(estado -> {
                    estado.setConectado(false);
                    estado.setUltimaActividad(LocalDateTime.now());
                    estadoConexionRepository.save(estado);
                });
    }

    @Override
    public boolean estaConectado(Long salaId, Long usuarioId) {
        return estadoConexionRepository.findBySalaIdAndUsuarioId(salaId, usuarioId)
                .map(EstadoConexionEntidad::getConectado)
                .orElse(true); // por defecto, asumir conectado si no hay registro
    }

    @Override
    public List<EstadoConexionDTO> obtenerEstados(Long salaId) {
        List<EstadoConexionEntidad> estados = estadoConexionRepository.findBySalaId(salaId);
        List<EstadoConexionDTO> result = new ArrayList<>();
        for (EstadoConexionEntidad e : estados) {
            EstadoConexionDTO dto = new EstadoConexionDTO();
            dto.setUsuarioId(e.getUsuarioId());
            dto.setConectado(e.getConectado());
            usuarioRepository.findById(e.getUsuarioId())
                    .ifPresent(u -> dto.setNombreUsuario(u.getNombre()));
            result.add(dto);
        }
        return result;
    }

    @Override
    @Transactional
    public void limpiarEstados(Long salaId) {
        estadoConexionRepository.deleteBySalaId(salaId);
    }
}
