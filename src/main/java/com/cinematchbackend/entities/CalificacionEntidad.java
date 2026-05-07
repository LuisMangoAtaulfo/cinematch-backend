package com.cinematchbackend.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "calificaciones")
public class CalificacionEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer valor;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntidad usuario;

    @OneToOne
    @JoinColumn(name = "sala_id", nullable = false)
    private SalaEntidad sala;
}