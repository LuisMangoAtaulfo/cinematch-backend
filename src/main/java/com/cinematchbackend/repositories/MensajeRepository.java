package com.cinematchbackend.repositories;

import com.cinematchbackend.entities.MensajeEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MensajeRepository extends JpaRepository<MensajeEntidad, Long> {
    List<MensajeEntidad> findBySalaIdOrderByFechaEnvioAsc(Long salaId);
    // MensajeRepository.java
    @Transactional
    void deleteBySalaId(Long salaId);
}