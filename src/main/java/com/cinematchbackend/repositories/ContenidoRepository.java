package com.cinematchbackend.repositories;

import com.cinematchbackend.entities.ContenidoEntidad;
import com.cinematchbackend.enums.GeneroContenido;
import com.cinematchbackend.enums.TipoContenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContenidoRepository extends JpaRepository<ContenidoEntidad, String> {

    List<ContenidoEntidad> findByTipo(TipoContenido tipo);
    List<ContenidoEntidad> findByGenero(GeneroContenido genero);
    List<ContenidoEntidad> findByTipoAndGenero(TipoContenido tipo, GeneroContenido genero);

    @Query("SELECT DISTINCT c FROM ContenidoEntidad c JOIN c.plataformas cp JOIN cp.plataforma p WHERE p.nombre = :nombre")
    List<ContenidoEntidad> findByPlataformaNombre(@Param("nombre") String nombre);

    @Query("SELECT DISTINCT c FROM ContenidoEntidad c JOIN c.plataformas cp JOIN cp.plataforma p WHERE p.nombre = :nombre AND c.tipo = :tipo")
    List<ContenidoEntidad> findByPlataformaNombreAndTipo(@Param("nombre") String nombre, @Param("tipo") TipoContenido tipo);

    @Query("SELECT DISTINCT c FROM ContenidoEntidad c JOIN c.plataformas cp JOIN cp.plataforma p WHERE p.nombre = :nombre AND c.genero = :genero")
    List<ContenidoEntidad> findByPlataformaNombreAndGenero(@Param("nombre") String nombre, @Param("genero") GeneroContenido genero);

    @Query("SELECT DISTINCT c FROM ContenidoEntidad c JOIN c.plataformas cp JOIN cp.plataforma p WHERE p.nombre = :nombre AND c.tipo = :tipo AND c.genero = :genero")
    List<ContenidoEntidad> findByPlataformaNombreAndTipoAndGenero(@Param("nombre") String nombre, @Param("tipo") TipoContenido tipo, @Param("genero") GeneroContenido genero);
}