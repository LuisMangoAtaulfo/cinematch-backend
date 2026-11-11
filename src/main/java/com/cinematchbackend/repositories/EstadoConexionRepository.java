package com.cinematchbackend.repositories;

import com.cinematchbackend.entities.EstadoConexionEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface EstadoConexionRepository extends JpaRepository<EstadoConexionEntidad, Long> {
    List<EstadoConexionEntidad> findBySalaId(Long salaId);
    Optional<EstadoConexionEntidad> findBySalaIdAndUsuarioId(Long salaId, Long usuarioId);

    @Transactional
    void deleteBySalaId(Long salaId);
}
