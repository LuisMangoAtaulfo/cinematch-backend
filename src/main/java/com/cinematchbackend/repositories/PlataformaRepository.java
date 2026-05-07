package com.cinematchbackend.repositories;

import com.cinematchbackend.entities.PlataformaEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlataformaRepository extends JpaRepository<PlataformaEntidad, Long> {
    List<PlataformaEntidad> findByHabilitadaTrue();
}