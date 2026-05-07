package com.cinematchbackend.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "evaluaciones")
public class EvaluacionEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean decision;

    @ManyToOne
    @JoinColumn(name = "sala_id", nullable = false)
    private SalaEntidad sala;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntidad usuario;

    @ManyToOne
    @JoinColumn(name = "contenido_id", nullable = false)
    private ContenidoEntidad contenido;
}