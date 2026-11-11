package com.cinematchbackend.services.impl;

import com.cinematchbackend.dto.response.MetricasHistorialResponseDTO;
import com.cinematchbackend.dto.response.MetricasResponseDTO;
import com.cinematchbackend.dto.response.MetricasUsuarioResponseDTO;
import com.cinematchbackend.enums.EstadoSala;
import com.cinematchbackend.repositories.CalificacionRepository;
import com.cinematchbackend.repositories.EvaluacionRepository;
import com.cinematchbackend.repositories.MatchRepository;
import com.cinematchbackend.repositories.SalaRepository;
import com.cinematchbackend.services.interfaces.MetricasService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricasServiceImpl implements MetricasService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final SalaRepository salaRepository;
    private final MatchRepository matchRepository;
    private final CalificacionRepository calificacionRepository;
    private final EvaluacionRepository evaluacionRepository;

    @Override
    public MetricasResponseDTO procesarMetricas() {
        MetricasResponseDTO dto = new MetricasResponseDTO();
        dto.setTotalSalas(salaRepository.count());
        dto.setSalasActivas(salaRepository.countByEstado(EstadoSala.ACTIVA));
        dto.setTotalMatches(matchRepository.count());
        dto.setPromedioCalificacion(calificacionRepository.calcularPromedio());
        dto.setFiltroActivo(false);
        return dto;
    }

    /**
     * R-BE-04: Métricas con filtros de fecha opcionales.
     */
    @Override
    public MetricasResponseDTO procesarMetricas(String desde, String hasta) {
        boolean hayFiltros = (desde != null && !desde.isBlank())
                           || (hasta != null && !hasta.isBlank());

        // Sin filtros: retornar todo
        if (!hayFiltros) {
            return procesarMetricas();
        }

        LocalDateTime inicio = (desde != null && !desde.isBlank())
                ? LocalDate.parse(desde, FORMATTER).atStartOfDay()
                : null;
        LocalDateTime fin = (hasta != null && !hasta.isBlank())
                ? LocalDate.parse(hasta, FORMATTER).atTime(LocalTime.MAX)
                : null;

        MetricasResponseDTO dto = new MetricasResponseDTO();
        dto.setTotalSalas(salaRepository.countByFechaCreacionFiltrado(inicio, fin));
        dto.setSalasActivas(salaRepository.countByEstadoAndFechaCreacionFiltrado(EstadoSala.ACTIVA, inicio, fin));
        dto.setTotalMatches(matchRepository.countByFechaCreacionSalaFiltrado(inicio, fin));

        Double promedio = (inicio != null && fin != null)
                ? calificacionRepository.calcularPromedioByFechaBetween(inicio, fin)
                : calificacionRepository.calcularPromedio();
        dto.setPromedioCalificacion(promedio != null ? promedio : 0.0);
        dto.setFiltroActivo(true);
        return dto;
    }

    @Override
    public List<MetricasHistorialResponseDTO> obtenerHistorial() {
        return salaRepository.findAllFechasCreacion()
                .stream()
                .map(LocalDateTime::toLocalDate)
                .distinct()
                .sorted()
                .map(this::buildHistorialForDate)
                .toList();
    }

    @Override
    public MetricasUsuarioResponseDTO procesarMetricasUsuario(Long usuarioId) {
        MetricasUsuarioResponseDTO dto = new MetricasUsuarioResponseDTO();
        dto.setTotalSalas(salaRepository.findAllByUsuario(usuarioId).stream().count());
        dto.setSalasActivas(salaRepository.countByEstadoAndUsuario(EstadoSala.ACTIVA, usuarioId));
        dto.setTotalMatches(matchRepository.countByUsuario(usuarioId));
        Double promedio = calificacionRepository.calcularPromedioByUsuario(usuarioId);
        dto.setPromedioCalificacion(promedio != null ? promedio : 0.0);
        dto.setTotalEvaluaciones(evaluacionRepository.countByUsuarioId(usuarioId));
        return dto;
    }

    @Override
    public List<MetricasHistorialResponseDTO> obtenerHistorialUsuario(Long usuarioId) {
        return salaRepository.findAllFechasCreacionByUsuario(usuarioId)
                .stream()
                .map(LocalDateTime::toLocalDate)
                .distinct()
                .sorted()
                .map(date -> buildHistorialUsuarioForDate(usuarioId, date))
                .toList();
    }

    private MetricasHistorialResponseDTO buildHistorialForDate(LocalDate date) {
        LocalDateTime inicio = date.atStartOfDay();
        LocalDateTime fin = date.atTime(LocalTime.MAX);

        Double promedio = calificacionRepository
                .calcularPromedioBySalaFechaCreacionBetween(inicio, fin);

        MetricasHistorialResponseDTO dto = new MetricasHistorialResponseDTO();
        dto.setFecha(inicio);
        dto.setTotalSalas(
                (long) salaRepository.findByFechaCreacionBetween(inicio, fin).size());
        dto.setSalasActivas(
                salaRepository.countByEstadoAndFechaCreacionBetween(EstadoSala.ACTIVA, inicio, fin));
        dto.setTotalMatches(
                matchRepository.countBySalaFechaCreacionBetween(inicio, fin));
        dto.setPromedioCalificacion(promedio != null ? promedio : 0.0);
        return dto;
    }

    private MetricasHistorialResponseDTO buildHistorialUsuarioForDate(Long usuarioId, LocalDate date) {
        LocalDateTime inicio = date.atStartOfDay();
        LocalDateTime fin = date.atTime(LocalTime.MAX);

        Double promedio = calificacionRepository
                .calcularPromedioByUsuarioAndFechaCreacionBetween(usuarioId, inicio, fin);

        MetricasHistorialResponseDTO dto = new MetricasHistorialResponseDTO();
        dto.setFecha(inicio);
        dto.setTotalSalas(
                (long) salaRepository.findByUsuarioAndFechaCreacionBetween(usuarioId, inicio, fin).size());
        dto.setSalasActivas(
                salaRepository.findByUsuarioAndFechaCreacionBetween(usuarioId, inicio, fin)
                        .stream()
                        .filter(s -> s.getEstado() == EstadoSala.ACTIVA)
                        .count());
        dto.setTotalMatches(
                matchRepository.countByUsuarioAndFechaCreacionBetween(usuarioId, inicio, fin));
        dto.setPromedioCalificacion(promedio != null ? promedio : 0.0);
        return dto;
    }
}
