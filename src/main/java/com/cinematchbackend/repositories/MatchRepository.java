package com.cinematchbackend.repositories;

import com.cinematchbackend.entities.MatchEntidad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<MatchEntidad, Long> {
    List<MatchEntidad> findBySalaId(Long salaId);
}