package com.cinematchbackend.repositories;

import com.cinematchbackend.entities.EvaluacionEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvaluacionRepository extends JpaRepository<EvaluacionEntidad, Long> {
    List<EvaluacionEntidad> findBySalaId(Long salaId);
    List<EvaluacionEntidad> findBySalaIdAndContenidoContenidoId(Long salaId, String contenidoId);
    Optional<EvaluacionEntidad> findBySalaIdAndUsuarioIdAndContenidoContenidoId(Long salaId, Long usuarioId, String contenidoId);
}