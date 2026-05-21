package com.cinematchbackend.repositories;

import com.cinematchbackend.entities.ContenidoEntidad;
import com.cinematchbackend.enums.GeneroContenido;
import com.cinematchbackend.enums.TipoContenido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContenidoRepository extends JpaRepository<ContenidoEntidad, String> {

    // ── Filtros sin plataforma ────────────────────────────────────────────────

    List<ContenidoEntidad> findByTipo(TipoContenido tipo);

    List<ContenidoEntidad> findByGenero(GeneroContenido genero);

    List<ContenidoEntidad> findByTipoAndGenero(TipoContenido tipo, GeneroContenido genero);

    // ── Filtros por año (sin plataforma) ─────────────────────────────────────

    List<ContenidoEntidad> findByAnio(Integer anio);

    List<ContenidoEntidad> findByAnioAndTipo(Integer anio, TipoContenido tipo);

    List<ContenidoEntidad> findByAnioAndGenero(Integer anio, GeneroContenido genero);

    List<ContenidoEntidad> findByAnioAndTipoAndGenero(Integer anio, TipoContenido tipo, GeneroContenido genero);

    // ── Filtros con plataforma (sin año) ─────────────────────────────────────

    @Query("SELECT DISTINCT c FROM ContenidoEntidad c JOIN c.plataformas cp JOIN cp.plataforma p WHERE p.nombre = :nombre")
    List<ContenidoEntidad> findByPlataformaNombre(@Param("nombre") String nombre);

    @Query("SELECT DISTINCT c FROM ContenidoEntidad c JOIN c.plataformas cp JOIN cp.plataforma p WHERE p.nombre = :nombre AND c.tipo = :tipo")
    List<ContenidoEntidad> findByPlataformaNombreAndTipo(@Param("nombre") String nombre, @Param("tipo") TipoContenido tipo);

    @Query("SELECT DISTINCT c FROM ContenidoEntidad c JOIN c.plataformas cp JOIN cp.plataforma p WHERE p.nombre = :nombre AND c.genero = :genero")
    List<ContenidoEntidad> findByPlataformaNombreAndGenero(@Param("nombre") String nombre, @Param("genero") GeneroContenido genero);

    @Query("SELECT DISTINCT c FROM ContenidoEntidad c JOIN c.plataformas cp JOIN cp.plataforma p WHERE p.nombre = :nombre AND c.tipo = :tipo AND c.genero = :genero")
    List<ContenidoEntidad> findByPlataformaNombreAndTipoAndGenero(@Param("nombre") String nombre, @Param("tipo") TipoContenido tipo, @Param("genero") GeneroContenido genero);

    // ── Filtros con plataforma + año ─────────────────────────────────────────

    @Query("SELECT DISTINCT c FROM ContenidoEntidad c JOIN c.plataformas cp JOIN cp.plataforma p WHERE p.nombre = :nombre AND c.anio = :anio")
    List<ContenidoEntidad> findByPlataformaNombreAndAnio(@Param("nombre") String nombre, @Param("anio") Integer anio); // ← NUEVO

    @Query("SELECT DISTINCT c FROM ContenidoEntidad c JOIN c.plataformas cp JOIN cp.plataforma p WHERE p.nombre = :nombre AND c.anio = :anio AND c.tipo = :tipo")
    List<ContenidoEntidad> findByPlataformaNombreAndAnioAndTipo(@Param("nombre") String nombre, @Param("anio") Integer anio, @Param("tipo") TipoContenido tipo); // ← NUEVO

    @Query("SELECT DISTINCT c FROM ContenidoEntidad c JOIN c.plataformas cp JOIN cp.plataforma p WHERE p.nombre = :nombre AND c.anio = :anio AND c.genero = :genero")
    List<ContenidoEntidad> findByPlataformaNombreAndAnioAndGenero(@Param("nombre") String nombre, @Param("anio") Integer anio, @Param("genero") GeneroContenido genero); // ← NUEVO

    @Query("SELECT DISTINCT c FROM ContenidoEntidad c JOIN c.plataformas cp JOIN cp.plataforma p WHERE p.nombre = :nombre AND c.anio = :anio AND c.tipo = :tipo AND c.genero = :genero")
    List<ContenidoEntidad> findByPlataformaNombreAndAnioAndTipoAndGenero(@Param("nombre") String nombre, @Param("anio") Integer anio, @Param("tipo") TipoContenido tipo, @Param("genero") GeneroContenido genero); // ← NUEVO
}