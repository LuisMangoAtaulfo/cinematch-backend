package com.cinematchbackend.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Persiste el estado de conexión de un usuario dentro de una sala activa (R-BE-10).
 * Permite al frontend consultar si el compañero está conectado, y evita
 * evaluar contenido cuando el usuario está desconectado.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "estados_conexion",
       uniqueConstraints = @UniqueConstraint(columnNames = {"sala_id", "usuario_id"}))
public class EstadoConexionEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sala_id", nullable = false)
    private Long salaId;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Boolean conectado = true;

    @Column(nullable = false)
    private LocalDateTime ultimaActividad = LocalDateTime.now();
}
