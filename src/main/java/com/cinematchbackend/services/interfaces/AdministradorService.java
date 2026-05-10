package com.cinematchbackend.services.interfaces;

import com.cinematchbackend.entities.AdministradorEntidad;
import com.cinematchbackend.entities.UsuarioEntidad;

public interface AdministradorService {
    String validarCredenciales(String correo, String password);
    void invalidarToken(String token);
    AdministradorEntidad crearUsuario (String correo, String password);

}
