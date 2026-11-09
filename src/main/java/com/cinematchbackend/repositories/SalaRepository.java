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

    @Query("SELECT s FROM SalaEntidad s WHERE s.usuario1.id = :usuarioId OR s.usuario2.id = :usuarioId")
    List<SalaEntidad> findAllByUsuario(@Param("usuarioId") Long usuarioId);

    @Query("SELECT s.fechaCreacion FROM SalaEntidad s WHERE s.usuario1.id = :usuarioId OR s.usuario2.id = :usuarioId")
    List<LocalDateTime> findAllFechasCreacionByUsuario(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(s) FROM SalaEntidad s WHERE s.estado = :estado AND (s.usuario1.id = :usuarioId OR s.usuario2.id = :usuarioId)")
    long countByEstadoAndUsuario(@Param("estado") EstadoSala estado, @Param("usuarioId") Long usuarioId);

    @Query("SELECT s FROM SalaEntidad s WHERE (s.usuario1.id = :usuarioId OR s.usuario2.id = :usuarioId) AND s.fechaCreacion BETWEEN :inicio AND :fin")
    List<SalaEntidad> findByUsuarioAndFechaCreacionBetween(@Param("usuarioId") Long usuarioId,
                                                            @Param("inicio") LocalDateTime inicio,
                                                            @Param("fin") LocalDateTime fin);
}