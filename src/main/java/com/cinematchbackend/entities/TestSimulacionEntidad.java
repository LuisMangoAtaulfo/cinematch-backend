package com.cinematchbackend.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Registra cada simulación ejecutada para tests automatizados (R-BE-09).
 * Auditoría de las acciones de prueba realizadas vía GraphQL.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "test_simulaciones")
public class TestSimulacionEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipoSimulacion;

    @Column(nullable = false)
    private Long salaId;

    @Column(nullable = false)
    private Long usuarioId;

    private String detalle;

    @Column(nullable = false)
    private LocalDateTime fechaEjecucion = LocalDateTime.now();
}
