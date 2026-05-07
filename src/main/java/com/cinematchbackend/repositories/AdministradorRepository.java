package com.cinematchbackend.repositories;

import com.cinematchbackend.entities.AdministradorEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdministradorRepository extends JpaRepository<AdministradorEntidad, Long> {
    Optional<AdministradorEntidad> findByCorreo(String correo);
}