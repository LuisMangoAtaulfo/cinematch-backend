package com.cinematchbackend.repositories;

import com.cinematchbackend.entities.MatchEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<MatchEntidad, Long> {
    List<MatchEntidad> findBySalaId(Long salaId);

    @Query("SELECT m FROM MatchEntidad m WHERE m.sala.id = :salaId AND m.contenido.contenidoId = :contenidoId")
    Optional<MatchEntidad> findBySalaIdAndContenidoContenidoId(@Param("salaId") Long salaId,
                                                                @Param("contenidoId") String contenidoId);

    /**
     * Matches históricos de un usuario (como usuario1 o usuario2 de la sala).
     */
    @Query("SELECT m FROM MatchEntidad m WHERE m.sala.usuario1.id = :usuarioId OR m.sala.usuario2.id = :usuarioId")
    List<MatchEntidad> findByUsuarioEnSala(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(m) FROM MatchEntidad m WHERE m.sala.fechaCreacion BETWEEN :inicio AND :fin")
    long countBySalaFechaCreacionBetween(@Param("inicio") LocalDateTime inicio,
                                         @Param("fin") LocalDateTime fin);

    // R-BE-04: Filtros de fecha opcionales
    @Query("SELECT COUNT(m) FROM MatchEntidad m " +
           "WHERE (:inicio IS NULL OR m.sala.fechaCreacion >= :inicio) " +
           "AND (:fin IS NULL OR m.sala.fechaCreacion <= :fin)")
    long countByFechaCreacionSalaFiltrado(@Param("inicio") LocalDateTime inicio,
                                          @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(m) FROM MatchEntidad m WHERE m.sala.usuario1.id = :usuarioId OR m.sala.usuario2.id = :usuarioId")
    long countByUsuario(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(m) FROM MatchEntidad m WHERE (m.sala.usuario1.id = :usuarioId OR m.sala.usuario2.id = :usuarioId) AND m.sala.fechaCreacion BETWEEN :inicio AND :fin")
    long countByUsuarioAndFechaCreacionBetween(@Param("usuarioId") Long usuarioId,
                                               @Param("inicio") LocalDateTime inicio,
                                               @Param("fin") LocalDateTime fin);
}
