package com.cinematchbackend.services.interfaces;

import com.cinematchbackend.dto.request.PerfilUpdateDTO;
import com.cinematchbackend.dto.request.UsuarioRegistroDTO;
import com.cinematchbackend.dto.response.UsuarioResponseDTO;
import com.cinematchbackend.entities.UsuarioEntidad;

public interface UsuarioService {
    UsuarioEntidad crearUsuario(UsuarioRegistroDTO dto);
    boolean validarCorreoDisponible(String correo);
    String validarCredenciales(String correo, String password);
    void invalidarToken(String token);
    UsuarioResponseDTO actualizarPerfil(Long id, PerfilUpdateDTO dto);
    String obtenerNombreUsuarioLogeado(String correo);
    UsuarioResponseDTO obtenerDatosUsuarioLogeado(Long id);
}