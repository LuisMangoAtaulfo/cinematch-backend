package com.cinematchbackend.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "matches")
public class MatchEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fechaDeteccion = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "sala_id", nullable = false)
    private SalaEntidad sala;

    @ManyToOne
    @JoinColumn(name = "contenido_id", nullable = false)
    private ContenidoEntidad contenido;
}