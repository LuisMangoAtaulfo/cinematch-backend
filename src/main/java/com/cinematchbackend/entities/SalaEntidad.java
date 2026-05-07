package com.cinematchbackend.entities;

import com.cinematchbackend.enums.EstadoSala;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "salas")
public class SalaEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSala estado = EstadoSala.ESPERANDO;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "usuario1_id")
    private UsuarioEntidad usuario1;

    @ManyToOne
    @JoinColumn(name = "usuario2_id")
    private UsuarioEntidad usuario2;

    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MensajeEntidad> mensajes = new ArrayList<>();

    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvaluacionEntidad> evaluaciones = new ArrayList<>();

    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL)
    private List<MatchEntidad> matches = new ArrayList<>();

    @OneToOne(mappedBy = "sala", cascade = CascadeType.ALL, orphanRemoval = true)
    private FiltroEntidad filtro;

    @OneToOne(mappedBy = "sala", cascade = CascadeType.ALL, orphanRemoval = true)
    private CalificacionEntidad calificacion;
}