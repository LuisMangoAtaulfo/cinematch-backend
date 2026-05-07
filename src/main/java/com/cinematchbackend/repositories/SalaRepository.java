package com.cinematchbackend.repositories;

import com.cinematchbackend.entities.SalaEntidad;
import com.cinematchbackend.enums.EstadoSala;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SalaRepository extends JpaRepository<SalaEntidad, Long> {
    Optional<SalaEntidad> findByCodigo(String codigo);
    long countByEstado(EstadoSala estado);
    List<SalaEntidad> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
}