package com.cinematchbackend.entities;

import com.cinematchbackend.enums.GeneroContenido;
import com.cinematchbackend.enums.TipoContenido;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "contenidos")
public class ContenidoEntidad {

    @Id
    private String contenidoId;

    @Column(nullable = false)
    private String titulo;

    private Integer anio;

    @Enumerated(EnumType.STRING)
    private TipoContenido tipo;

    @Enumerated(EnumType.STRING)
    private GeneroContenido genero;

    private String imagen;

    @OneToMany(mappedBy = "contenido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContenidoPlataformaEntidad> plataformas = new ArrayList<>();
}