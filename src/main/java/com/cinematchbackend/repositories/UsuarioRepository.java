package com.cinematchbackend.repositories;

import com.cinematchbackend.entities.UsuarioEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntidad, Long> {
    Optional<UsuarioEntidad> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
}