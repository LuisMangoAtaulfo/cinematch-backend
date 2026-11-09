package com.cinematchbackend.repositories;

import com.cinematchbackend.entities.MatchEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchRepository extends JpaRepository<MatchEntidad, Long> {
    List<MatchEntidad> findBySalaId(Long salaId);
    @Query("SELECT COUNT(m) FROM MatchEntidad m WHERE m.sala.fechaCreacion BETWEEN :inicio AND :fin")
    long countBySalaFechaCreacionBetween(@Param("inicio") LocalDateTime inicio,
                                         @Param("fin") LocalDateTime fin);

    @Query("SELECT COUNT(m) FROM MatchEntidad m WHERE m.sala.usuario1.id = :usuarioId OR m.sala.usuario2.id = :usuarioId")
    long countByUsuario(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(m) FROM MatchEntidad m WHERE (m.sala.usuario1.id = :usuarioId OR m.sala.usuario2.id = :usuarioId) AND m.sala.fechaCreacion BETWEEN :inicio AND :fin")
    long countByUsuarioAndFechaCreacionBetween(@Param("usuarioId") Long usuarioId,
                                               @Param("inicio") LocalDateTime inicio,
                                               @Param("fin") LocalDateTime fin);
}