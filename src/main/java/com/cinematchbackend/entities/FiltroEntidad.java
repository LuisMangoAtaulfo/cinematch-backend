package com.cinematchbackend.entities;

import com.cinematchbackend.enums.GeneroContenido;
import com.cinematchbackend.enums.TipoContenido;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "filtros")
public class FiltroEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoContenido tipo;

    @Enumerated(EnumType.STRING)
    private GeneroContenido genero;

    private String plataforma;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    private Integer anio;

    @OneToOne
    @JoinColumn(name = "sala_id", nullable = false)
    private SalaEntidad sala;
}