package com.cinematchbackend.repositories;

import com.cinematchbackend.entities.FiltroEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FiltroRepository extends JpaRepository<FiltroEntidad, Long> {
    Optional<FiltroEntidad> findBySalaId(Long salaId);
}