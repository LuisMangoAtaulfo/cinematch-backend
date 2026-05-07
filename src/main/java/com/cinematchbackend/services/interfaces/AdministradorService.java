package com.cinematchbackend.services.interfaces;

public interface AdministradorService {
    String validarCredenciales(String correo, String password);
    void invalidarToken(String token);

}
