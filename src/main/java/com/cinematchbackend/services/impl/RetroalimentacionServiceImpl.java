package com.cinematchbackend.services.impl;

import com.cinematchbackend.dto.response.CalificacionResponseDTO;
import com.cinematchbackend.repositories.CalificacionRepository;
import com.cinematchbackend.services.interfaces.RetroalimentacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RetroalimentacionServiceImpl implements RetroalimentacionService {

    private final CalificacionRepository calificacionRepository;

    @Override
    public List<CalificacionResponseDTO> procesarRetroalimentacion() {
        return calificacionRepository.findAll().stream()
                .map(c -> {
                    CalificacionResponseDTO dto = new CalificacionResponseDTO();
                    dto.setId(c.getId());
                    dto.setValor(c.getValor());
                    dto.setFecha(c.getFecha());
                    dto.setUsuarioId(c.getUsuario().getId());
                    dto.setSalaId(c.getSala().getId());
                    return dto;
                }).toList();
    }

    @Override
    public Double obtenerPromedio() {
        return calificacionRepository.calcularPromedio();
    }
}