package com.cinematchbackend.services.impl;

import com.cinematchbackend.dto.request.CalificacionRequestDTO;
import com.cinematchbackend.entities.CalificacionEntidad;
import com.cinematchbackend.entities.SalaEntidad;
import com.cinematchbackend.entities.UsuarioEntidad;
import com.cinematchbackend.exceptions.SalaNoEncontradaException;
import com.cinematchbackend.exceptions.UsuarioNoEncontradoException;
import com.cinematchbackend.repositories.CalificacionRepository;
import com.cinematchbackend.repositories.SalaRepository;
import com.cinematchbackend.repositories.UsuarioRepository;
import com.cinematchbackend.services.interfaces.CalificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalificacionServiceImpl implements CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final SalaRepository salaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public void registrarCalificacion(CalificacionRequestDTO dto) {
        SalaEntidad sala = salaRepository.findById(dto.getSalaId())
                .orElseThrow(() -> new SalaNoEncontradaException("Sala no encontrada: " + dto.getSalaId()));
        UsuarioEntidad usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + dto.getUsuarioId()));
        CalificacionEntidad calificacion = new CalificacionEntidad();
        calificacion.setSala(sala);
        calificacion.setUsuario(usuario);
        calificacion.setValor(dto.getValor());
        calificacionRepository.save(calificacion);
    }
}