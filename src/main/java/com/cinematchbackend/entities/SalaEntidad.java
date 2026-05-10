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

    private SalaEntidad(Builder builder) {
        this.codigo       = builder.codigo;
        this.estado       = builder.estado;
        this.fechaCreacion = builder.fechaCreacion;
        this.usuario1     = builder.usuario1;
        this.usuario2     = builder.usuario2;
    }

    public static class Builder {
        private String codigo;
        private EstadoSala estado = EstadoSala.ESPERANDO;
        private LocalDateTime fechaCreacion = LocalDateTime.now();
        private UsuarioEntidad usuario1;
        private UsuarioEntidad usuario2;

        public Builder codigo(String codigo) {
            this.codigo = codigo;
            return this;
        }

        public Builder estado(EstadoSala estado) {
            this.estado = estado;
            return this;
        }

        public Builder fechaCreacion(LocalDateTime fechaCreacion) {
            this.fechaCreacion = fechaCreacion;
            return this;
        }

        public Builder usuario1(UsuarioEntidad usuario1) {
            this.usuario1 = usuario1;
            return this;
        }

        public Builder usuario2(UsuarioEntidad usuario2) {
            this.usuario2 = usuario2;
            return this;
        }

        public SalaEntidad build() {
            return new SalaEntidad(this);
        }
    }
}