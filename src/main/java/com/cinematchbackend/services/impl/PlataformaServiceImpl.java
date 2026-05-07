package com.cinematchbackend.services.impl;

import com.cinematchbackend.dto.request.PlataformaUpdateDTO;
import com.cinematchbackend.dto.response.PlataformaResponseDTO;
import com.cinematchbackend.entities.PlataformaEntidad;
import com.cinematchbackend.exceptions.PlataformaNoEncontradaException;
import com.cinematchbackend.repositories.PlataformaRepository;
import com.cinematchbackend.services.interfaces.PlataformaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlataformaServiceImpl implements PlataformaService {

    private final PlataformaRepository plataformaRepository;

    @Override
    public List<PlataformaResponseDTO> listarPlataformas() {
        return plataformaRepository.findAll().stream()
                .map(this::mapearAResponse)
                .toList();
    }

    @Override
    public void actualizarEstado(PlataformaUpdateDTO dto) {
        PlataformaEntidad plataforma = plataformaRepository.findById(dto.getId())
                .orElseThrow(() -> new PlataformaNoEncontradaException("Plataforma no encontrada: " + dto.getId()));
        plataforma.setHabilitada(dto.getHabilitada());
        plataformaRepository.save(plataforma);
    }

    private PlataformaResponseDTO mapearAResponse(PlataformaEntidad plataforma) {
        PlataformaResponseDTO dto = new PlataformaResponseDTO();
        dto.setId(plataforma.getId());
        dto.setNombre(plataforma.getNombre());
        dto.setHabilitada(plataforma.getHabilitada());
        return dto;
    }
}