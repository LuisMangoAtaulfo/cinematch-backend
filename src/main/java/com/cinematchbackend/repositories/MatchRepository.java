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
}