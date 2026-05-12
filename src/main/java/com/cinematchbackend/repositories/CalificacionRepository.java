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

    @Query("SELECT AVG(c.valor) FROM CalificacionEntidad c")
    Double calcularPromedio();

    @Query("SELECT AVG(c.valor) FROM CalificacionEntidad c WHERE c.sala.fechaCreacion BETWEEN :inicio AND :fin")
    Double calcularPromedioBySalaFechaCreacionBetween(@Param("inicio") LocalDateTime inicio,
                                                      @Param("fin") LocalDateTime fin);
}