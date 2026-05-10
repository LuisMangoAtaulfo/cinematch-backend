package com.cinematchbackend.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "contenido_plataforma")
public class ContenidoPlataformaEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contenido_id", nullable = false)
    private ContenidoEntidad contenido;

    @ManyToOne
    @JoinColumn(name = "plataforma_id", nullable = false)
    private PlataformaEntidad plataforma;
}