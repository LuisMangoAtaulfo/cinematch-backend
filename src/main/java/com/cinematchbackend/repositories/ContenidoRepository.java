package com.cinematchbackend.repositories;

import com.cinematchbackend.entities.ContenidoEntidad;
import com.cinematchbackend.enums.GeneroContenido;
import com.cinematchbackend.enums.TipoContenido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContenidoRepository extends JpaRepository<ContenidoEntidad, String> {
    List<ContenidoEntidad> findByTipo(TipoContenido tipo);
    List<ContenidoEntidad> findByGenero(GeneroContenido genero);
    List<ContenidoEntidad> findByTipoAndGenero(TipoContenido tipo, GeneroContenido genero);
}