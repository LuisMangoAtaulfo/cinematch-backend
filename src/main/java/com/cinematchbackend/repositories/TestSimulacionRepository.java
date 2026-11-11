package com.cinematchbackend.repositories;

import com.cinematchbackend.entities.TestSimulacionEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestSimulacionRepository extends JpaRepository<TestSimulacionEntidad, Long> {
    List<TestSimulacionEntidad> findBySalaIdOrderByFechaEjecucionDesc(Long salaId);
    List<TestSimulacionEntidad> findByUsuarioId(Long usuarioId);
}
