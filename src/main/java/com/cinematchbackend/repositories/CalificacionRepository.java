package com.cinematchbackend.repositories;

import com.cinematchbackend.entities.CalificacionEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CalificacionRepository extends JpaRepository<CalificacionEntidad, Long> {
    List<CalificacionEntidad> findBySalaId(Long salaId);
    List<CalificacionEntidad> findByUsuarioId(Long usuarioId);
    Optional<CalificacionEntidad> findBySalaIdAndUsuarioId(Long salaId, Long usuarioId);
    boolean existsBySalaIdAndUsuarioId(Long salaId, Long usuarioId);

    @Query("SELECT AVG(c.valor) FROM CalificacionEntidad c")
    Double calcularPromedio();

    @Query("SELECT AVG(c.valor) FROM CalificacionEntidad c WHERE c.fecha BETWEEN :inicio AND :fin")
    Double calcularPromedioByFechaBetween(@Param("inicio") LocalDateTime inicio,
                                          @Param("fin") LocalDateTime fin);

    @Query("SELECT AVG(c.valor) FROM CalificacionEntidad c WHERE c.sala.fechaCreacion BETWEEN :inicio AND :fin")
    Double calcularPromedioBySalaFechaCreacionBetween(@Param("inicio") LocalDateTime inicio,
                                                       @Param("fin") LocalDateTime fin);

    @Query("SELECT c FROM CalificacionEntidad c WHERE c.fecha BETWEEN :inicio AND :fin")
    List<CalificacionEntidad> findByFechaBetween(@Param("inicio") LocalDateTime inicio,
                                                  @Param("fin") LocalDateTime fin);

    @Query("SELECT c.valor, COUNT(c) FROM CalificacionEntidad c GROUP BY c.valor ORDER BY c.valor")
    List<Object[]> calcularDistribucion();
}
