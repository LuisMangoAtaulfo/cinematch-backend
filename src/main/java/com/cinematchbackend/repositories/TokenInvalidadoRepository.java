package com.cinematchbackend.repositories;

import com.cinematchbackend.entities.TokenInvalidadoEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenInvalidadoRepository extends JpaRepository<TokenInvalidadoEntidad, Long> {
    boolean existsByToken(String token);
}