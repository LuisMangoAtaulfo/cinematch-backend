package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.request.PerfilUpdateDTO;
import com.cinematchbackend.dto.response.UsuarioResponseDTO;
import com.cinematchbackend.exceptions.NombreRequeridoException;
import com.cinematchbackend.exceptions.UnauthorizedException;
import com.cinematchbackend.services.interfaces.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UsuarioGraphQLController {

    private final UsuarioService usuarioService;

    @QueryMapping
    public UsuarioResponseDTO obtenerPerfil(@Argument Long id) {
        return usuarioService.obtenerDatosUsuarioLogeado(id);
    }

    /**
     * R-BE-04: Query miPerfil — retorna el perfil del usuario autenticado
     * sin necesidad de pasar un ID. Extrae el correo del SecurityContext
     * (establecido por JwtAuthenticationFilter) y busca el usuario.
     */
    @QueryMapping
    @PreAuthorize("isAuthenticated()")
    public UsuarioResponseDTO miPerfil() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || "anonymousUser".equals(auth.getName())) {
            throw new UnauthorizedException("Autenticación requerida");
        }
        String correo = auth.getName();
        log.info("Consultando miPerfil para correo: {}", correo);
        return usuarioService.obtenerDatosUsuarioPorCorreo(correo);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public UsuarioResponseDTO actualizarPerfil(@Argument Long id, @Argument PerfilUpdateDTO input) {
        // R-BE-01: Validar nombre requerido
        if (input.getNombre() == null || input.getNombre().isBlank()) {
            throw new NombreRequeridoException();
        }

        log.info("Actualizando perfil de usuario {}", id);
        return usuarioService.actualizarPerfil(id, input);
    }
}
