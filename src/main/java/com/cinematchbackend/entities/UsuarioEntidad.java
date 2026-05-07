package com.cinematchbackend.entities;


import com.cinematchbackend.enums.RolUsuario;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class UsuarioEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String correo;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol = RolUsuario.USUARIO;
}