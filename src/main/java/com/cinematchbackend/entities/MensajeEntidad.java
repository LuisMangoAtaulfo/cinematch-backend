package com.cinematchbackend.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "mensajes")
public class MensajeEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String texto;

    @Column(nullable = false)
    private LocalDateTime fechaEnvio = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "sala_id", nullable = false)
    private SalaEntidad sala;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntidad usuario;
}