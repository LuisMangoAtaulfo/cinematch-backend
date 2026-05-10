package com.cinematchbackend.services.impl;

import com.cinematchbackend.config.JwtService;
import com.cinematchbackend.dto.request.UsuarioRegistroDTO;
import com.cinematchbackend.entities.AdministradorEntidad;
import com.cinematchbackend.entities.UsuarioEntidad;
import com.cinematchbackend.enums.RolUsuario;
import com.cinematchbackend.exceptions.EmailAlreadyExistsException;
import com.cinematchbackend.exceptions.UsuarioNoEncontradoException;
import com.cinematchbackend.repositories.AdministradorRepository;
import com.cinematchbackend.services.interfaces.AdministradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdministradorServiceImpl implements AdministradorService {
    private final AdministradorRepository administradorRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String validarCredenciales(String correo, String password) {
        AdministradorEntidad admin = administradorRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + correo));
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new com.cinematchbackend.exceptions.BadCredentialsException("Credenciales incorrectas");
        }
        return jwtService.generarToken(admin.getId(),admin.getCorreo(), admin.getRol().name());
    }

    @Override
    public void invalidarToken(String token) {
        jwtService.invalidarToken(token);
    }

    @Override
    public AdministradorEntidad crearUsuario(String correo, String password) {
        if (!validarCorreoDisponible(correo)){
            throw new EmailAlreadyExistsException("El correo ya está en uso: " + correo);
        }
        AdministradorEntidad admin = new AdministradorEntidad();
        admin.setCorreo(correo);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRol(RolUsuario.USUARIO);
        return administradorRepository.save(admin);
    }
    public boolean validarCorreoDisponible(String correo) {
        return !administradorRepository.existsByCorreo(correo);
    }

}
