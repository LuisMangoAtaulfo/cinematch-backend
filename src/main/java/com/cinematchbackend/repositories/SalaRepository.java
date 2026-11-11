package com.cinematchbackend.repositories;

import com.cinematchbackend.entities.SalaEntidad;
import com.cinematchbackend.enums.EstadoSala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SalaRepository extends JpaRepository<SalaEntidad, Long> {
    Optional<SalaEntidad> findByCodigo(String codigo);
    long countByEstado(EstadoSala estado);

    @Query("SELECT s.fechaCreacion FROM SalaEntidad s")
    List<LocalDateTime> findAllFechasCreacion();
    List<SalaEntidad> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
    long countByEstadoAndFechaCreacionBetween(EstadoSala estado, LocalDateTime inicio, LocalDateTime fin);

    // R-BE-04: Filtros de fecha opcionales (null = sin filtro)
    @Query("SELECT COUNT(s) FROM SalaEntidad s " +
           "WHERE (:inicio IS NULL OR s.fechaCreacion >= :inicio) " +
           "AND (:fin IS NULL OR s.fechaCreacion <= :fin)")
    long countByFechaCreacionFiltrado(@Param("inicio") LocalDateTime inicio,
                                       @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(s) FROM SalaEntidad s " +
           "WHERE s.estado = :estado " +
           "AND (:inicio IS NULL OR s.fechaCreacion >= :inicio) " +
           "AND (:fin IS NULL OR s.fechaCreacion <= :fin)")
    long countByEstadoAndFechaCreacionFiltrado(@Param("estado") EstadoSala estado,
                                                @Param("inicio") LocalDateTime inicio,
                                                @Param("fin") LocalDateTime fin);
}
