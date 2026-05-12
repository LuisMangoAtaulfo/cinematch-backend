package com.cinematchbackend.services.impl;

import com.cinematchbackend.dto.response.MetricasHistorialResponseDTO;
import com.cinematchbackend.dto.response.MetricasResponseDTO;
import com.cinematchbackend.enums.EstadoSala;
import com.cinematchbackend.repositories.CalificacionRepository;
import com.cinematchbackend.repositories.MatchRepository;
import com.cinematchbackend.repositories.SalaRepository;
import com.cinematchbackend.services.interfaces.MetricasService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricasServiceImpl implements MetricasService {

    private final SalaRepository salaRepository;
    private final MatchRepository matchRepository;
    private final CalificacionRepository calificacionRepository;

    @Override
    public MetricasResponseDTO procesarMetricas() {
        MetricasResponseDTO dto = new MetricasResponseDTO();
        dto.setTotalSalas(salaRepository.count());
        dto.setSalasActivas(salaRepository.countByEstado(EstadoSala.ACTIVA));
        dto.setTotalMatches(matchRepository.count());
        dto.setPromedioCalificacion(calificacionRepository.calcularPromedio());
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
}