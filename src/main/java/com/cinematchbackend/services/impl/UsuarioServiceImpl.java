package com.cinematchbackend.services.impl;

import com.cinematchbackend.dto.request.PerfilUpdateDTO;
import com.cinematchbackend.dto.request.UsuarioRegistroDTO;
import com.cinematchbackend.dto.response.UsuarioResponseDTO;
import com.cinematchbackend.entities.UsuarioEntidad;
import com.cinematchbackend.enums.RolUsuario;
import com.cinematchbackend.exceptions.EmailAlreadyExistsException;
import com.cinematchbackend.exceptions.UsuarioNoEncontradoException;
import com.cinematchbackend.repositories.UsuarioRepository;
import com.cinematchbackend.services.interfaces.UsuarioService;
import com.cinematchbackend.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public UsuarioEntidad crearUsuario(UsuarioRegistroDTO dto) {
        if (!validarCorreoDisponible(dto.getCorreo())) {
            throw new EmailAlreadyExistsException("El correo ya está en uso: " + dto.getCorreo());
        }
        UsuarioEntidad usuario = new UsuarioEntidad();
        usuario.setNombre(dto.getNombre());
        usuario.setCorreo(dto.getCorreo());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(RolUsuario.USUARIO);
        return usuarioRepository.save(usuario);
    }

    @Override
    public boolean validarCorreoDisponible(String correo) {
        return !usuarioRepository.existsByCorreo(correo);
    }

    @Override
    public String validarCredenciales(String correo, String password) {
        UsuarioEntidad usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + correo));
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new com.cinematchbackend.exceptions.BadCredentialsException("Credenciales incorrectas");
        }
        return jwtService.generarToken(usuario.getId(),usuario.getCorreo(), usuario.getRol().name());
    }

    @Override
    public void invalidarToken(String token) {
        jwtService.invalidarToken(token);
    }

    @Override
    public UsuarioResponseDTO actualizarPerfil(Long id, PerfilUpdateDTO dto) {
        UsuarioEntidad usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + id));
        if (dto.getNombre() != null) usuario.setNombre(dto.getNombre());
        if (dto.getDescripcion() != null) usuario.setDescripcion(dto.getDescripcion());
        usuarioRepository.save(usuario);
        return mapearAResponse(usuario);
    }

    @Override
    public String obtenerNombreUsuarioLogeado(String correo) {
        Optional<UsuarioEntidad> usuario = usuarioRepository.findByCorreo(correo);
        if(usuario.isPresent()){
            UsuarioEntidad usuarioEntidad = usuario.get();
            return usuarioEntidad.getNombre();
        }
        else{
            usuario.orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + correo));
            return "";
        }
    }
    private UsuarioResponseDTO mapearAResponse(UsuarioEntidad usuario) {
        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setCorreo(usuario.getCorreo());
        response.setDescripcion(usuario.getDescripcion());
        response.setRol(usuario.getRol());
        return response;
    }


}