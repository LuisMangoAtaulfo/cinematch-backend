package com.cinematchbackend.services.impl;

import com.cinematchbackend.dto.response.CalificacionResponseDTO;
import com.cinematchbackend.dto.response.RetroalimentacionCompletaDTO;
import com.cinematchbackend.entities.CalificacionEntidad;
import com.cinematchbackend.repositories.CalificacionRepository;
import com.cinematchbackend.services.interfaces.RetroalimentacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RetroalimentacionServiceImpl implements RetroalimentacionService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final CalificacionRepository calificacionRepository;

    @Override
    public List<CalificacionResponseDTO> procesarRetroalimentacion() {
        return calificacionRepository.findAll().stream()
                .map(this::mapear)
                .toList();
    }

    @Override
    public Double obtenerPromedio() {
        Double promedio = calificacionRepository.calcularPromedio();
        return promedio != null ? promedio : 0.0;
    }

    @Override
    public RetroalimentacionCompletaDTO obtenerRetroalimentacionCompleta(String desde, String hasta) {
        boolean hayFiltros = (desde != null && !desde.isBlank())
                           || (hasta != null && !hasta.isBlank());

        List<CalificacionEntidad> calificaciones;

        if (desde != null && !desde.isBlank() && hasta != null && !hasta.isBlank()) {
            LocalDateTime d = LocalDateTime.parse(desde, FORMATTER);
            LocalDateTime h = LocalDateTime.parse(hasta, FORMATTER);
            calificaciones = calificacionRepository.findByFechaBetween(d, h);
        } else if (desde != null && !desde.isBlank()) {
            LocalDateTime d = LocalDateTime.parse(desde, FORMATTER);
            calificaciones = calificacionRepository.findAll().stream()
                    .filter(c -> c.getFecha().isAfter(d))
                    .toList();
        } else if (hasta != null && !hasta.isBlank()) {
            LocalDateTime h = LocalDateTime.parse(hasta, FORMATTER);
            calificaciones = calificacionRepository.findAll().stream()
                    .filter(c -> c.getFecha().isBefore(h))
                    .toList();
        } else {
            calificaciones = calificacionRepository.findAll();
        }

        // Distribución: array de 10 elementos (índice 0 = rating 1, índice 9 = rating 10)
        List<Integer> distribucion = new ArrayList<>(List.of(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        for (CalificacionEntidad c : calificaciones) {
            if (c.getValor() >= 1 && c.getValor() <= 10) {
                distribucion.set(c.getValor() - 1, distribucion.get(c.getValor() - 1) + 1);
            }
        }

        Double promedio = calificaciones.isEmpty() ? 0.0 :
                calificaciones.stream().mapToInt(CalificacionEntidad::getValor).average().orElse(0.0);

        RetroalimentacionCompletaDTO resultado = new RetroalimentacionCompletaDTO();
        resultado.setPromedio(promedio);
        resultado.setDistribucion(distribucion);
        resultado.setTotalCalificaciones(calificaciones.size());
        resultado.setCalificaciones(calificaciones.stream().map(this::mapear).toList());
        resultado.setFiltroActivo(hayFiltros);
        return resultado;
    }

    private CalificacionResponseDTO mapear(CalificacionEntidad c) {
        CalificacionResponseDTO dto = new CalificacionResponseDTO();
        dto.setId(c.getId());
        dto.setValor(c.getValor());
        dto.setFecha(c.getFecha());
        dto.setUsuarioId(c.getUsuario().getId());
        dto.setSalaId(c.getSala().getId());
        return dto;
    }
}
