package com.cinematchbackend.services.impl;

import com.cinematchbackend.dto.response.MetricasResponseDTO;
import com.cinematchbackend.enums.EstadoSala;
import com.cinematchbackend.repositories.CalificacionRepository;
import com.cinematchbackend.repositories.MatchRepository;
import com.cinematchbackend.repositories.SalaRepository;
import com.cinematchbackend.services.interfaces.MetricasService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}