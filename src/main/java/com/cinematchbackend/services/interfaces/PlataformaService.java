package com.cinematchbackend.services.interfaces;

import com.cinematchbackend.dto.request.PlataformaUpdateDTO;
import com.cinematchbackend.dto.response.PlataformaResponseDTO;

import java.util.List;

public interface PlataformaService {
    List<PlataformaResponseDTO> listarPlataformas();
    void actualizarEstado(PlataformaUpdateDTO dto);
}