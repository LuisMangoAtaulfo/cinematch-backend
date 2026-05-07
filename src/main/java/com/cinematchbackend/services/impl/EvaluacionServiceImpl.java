package com.cinematchbackend.services.impl;

import com.cinematchbackend.dto.request.EvaluacionRequestDTO;
import com.cinematchbackend.entities.ContenidoEntidad;
import com.cinematchbackend.entities.EvaluacionEntidad;
import com.cinematchbackend.entities.SalaEntidad;
import com.cinematchbackend.entities.UsuarioEntidad;
import com.cinematchbackend.exceptions.ContenidoNoEncontradoException;
import com.cinematchbackend.exceptions.EvaluacionDuplicadaException;
import com.cinematchbackend.exceptions.SalaNoEncontradaException;
import com.cinematchbackend.exceptions.UsuarioNoEncontradoException;
import com.cinematchbackend.observer.MatchObserver;
import com.cinematchbackend.repositories.ContenidoRepository;
import com.cinematchbackend.repositories.EvaluacionRepository;
import com.cinematchbackend.repositories.SalaRepository;
import com.cinematchbackend.repositories.UsuarioRepository;
import com.cinematchbackend.services.interfaces.EvaluacionService;
import com.cinematchbackend.services.interfaces.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluacionServiceImpl implements EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final SalaRepository salaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ContenidoRepository contenidoRepository;
    private final MatchService matchService;
    private final List<MatchObserver> observers;

    @Override
    public void registrarEvaluacion(EvaluacionRequestDTO dto) {
        evaluacionRepository.findBySalaIdAndUsuarioIdAndContenidoContenidoId(
                        dto.getSalaId(), dto.getUsuarioId(), dto.getContenidoId())
                .ifPresent(e -> { throw new EvaluacionDuplicadaException("Ya evaluaste este contenido"); });

        SalaEntidad sala = salaRepository.findById(dto.getSalaId())
                .orElseThrow(() -> new SalaNoEncontradaException("Sala no encontrada: " + dto.getSalaId()));
        UsuarioEntidad usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + dto.getUsuarioId()));
        ContenidoEntidad contenido = contenidoRepository.findById(dto.getContenidoId())
                .orElseThrow(() -> new ContenidoNoEncontradoException("Contenido no encontrado: " + dto.getContenidoId()));

        EvaluacionEntidad evaluacion = new EvaluacionEntidad();
        evaluacion.setSala(sala);
        evaluacion.setUsuario(usuario);
        evaluacion.setContenido(contenido);
        evaluacion.setDecision(dto.getDecision());
        evaluacionRepository.save(evaluacion);

        if (dto.getDecision() && compararEvaluaciones(dto.getSalaId(), dto.getContenidoId())) {
            matchService.registrarMatch(dto.getSalaId(), dto.getContenidoId());
            observers.forEach(o -> o.notificar(dto.getSalaId(), dto.getContenidoId()));
        }
    }

    @Override
    public boolean compararEvaluaciones(Long salaId, String contenidoId) {
        List<EvaluacionEntidad> evaluaciones = evaluacionRepository
                .findBySalaIdAndContenidoContenidoId(salaId, contenidoId);
        return evaluaciones.size() == 2 && evaluaciones.stream().allMatch(EvaluacionEntidad::getDecision);
    }
}