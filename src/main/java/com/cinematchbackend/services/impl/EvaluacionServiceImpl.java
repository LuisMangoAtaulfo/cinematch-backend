package com.cinematchbackend.services.impl;

import com.cinematchbackend.dto.request.EvaluacionRequestDTO;
import com.cinematchbackend.dto.response.EvaluacionNotificationDTO;
import com.cinematchbackend.entities.ContenidoEntidad;
import com.cinematchbackend.entities.EvaluacionEntidad;
import com.cinematchbackend.entities.SalaEntidad;
import com.cinematchbackend.entities.UsuarioEntidad;
import com.cinematchbackend.exceptions.ContenidoNoEncontradoException;
import com.cinematchbackend.exceptions.EvaluacionDuplicadaException;
import com.cinematchbackend.exceptions.SalaNoEncontradaException;
import com.cinematchbackend.exceptions.UsuarioDesconectadoException;
import com.cinematchbackend.exceptions.UsuarioNoEncontradoException;
import com.cinematchbackend.observer.MatchSubject;
import com.cinematchbackend.repositories.ContenidoRepository;
import com.cinematchbackend.repositories.EvaluacionRepository;
import com.cinematchbackend.repositories.SalaRepository;
import com.cinematchbackend.repositories.UsuarioRepository;
import com.cinematchbackend.services.interfaces.ConexionService;
import com.cinematchbackend.services.interfaces.EvaluacionService;
import com.cinematchbackend.services.interfaces.EventPublisherService;
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
    private final MatchSubject matchSubject;
    private final EventPublisherService eventPublisherService;
    private final ConexionService conexionService;

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

        // R-BE-10: verificar que el usuario está conectado
        if (!conexionService.estaConectado(dto.getSalaId(), dto.getUsuarioId())) {
            throw new UsuarioDesconectadoException("Usuario desconectado");
        }

        EvaluacionEntidad evaluacion = new EvaluacionEntidad();
        evaluacion.setSala(sala);
        evaluacion.setUsuario(usuario);
        evaluacion.setContenido(contenido);
        evaluacion.setDecision(dto.getDecision());
        evaluacionRepository.save(evaluacion);

        // Notificar al compañero que se recibió una evaluación
        publicarEvaluacion(dto.getSalaId(), usuario, dto.getContenidoId(), dto.getDecision());

        if (dto.getDecision() && compararEvaluaciones(dto.getSalaId(), dto.getContenidoId())) {
            matchService.registrarMatch(dto.getSalaId(), dto.getContenidoId());
            matchSubject.notificarObservers(dto.getSalaId(), dto.getContenidoId());
        }
    }

    @Override
    public boolean compararEvaluaciones(Long salaId, String contenidoId) {
        List<EvaluacionEntidad> evaluaciones = evaluacionRepository
                .findBySalaIdAndContenidoContenidoId(salaId, contenidoId);
        return evaluaciones.size() == 2 && evaluaciones.stream().allMatch(EvaluacionEntidad::getDecision);
    }

    private void publicarEvaluacion(Long salaId, UsuarioEntidad usuario, String contenidoId, Boolean decision) {
        EvaluacionNotificationDTO dto = new EvaluacionNotificationDTO();
        dto.setSalaId(salaId);
        dto.setUsuarioId(usuario.getId());
        dto.setContenidoId(contenidoId);
        dto.setDecision(decision);
        dto.setNombreUsuario(usuario.getNombre());
        eventPublisherService.publicarEvaluacion(salaId, dto);
    }
}
