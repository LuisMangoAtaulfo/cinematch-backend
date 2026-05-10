package com.cinematchbackend.repositories;

import com.cinematchbackend.entities.ContenidoPlataformaEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContenidoPlataformaRepository extends JpaRepository<ContenidoPlataformaEntidad, Long> {
    boolean existsByContenidoContenidoIdAndPlataformaNombre(String contenidoId, String nombrePlataforma);
    List<ContenidoPlataformaEntidad> findByPlataformaNombre(String nombrePlataforma);
}