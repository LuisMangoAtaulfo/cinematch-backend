package com.cinematchbackend.controllers;

import com.cinematchbackend.dto.response.AuthResponseDTO;
import com.cinematchbackend.entities.AdministradorEntidad;
import com.cinematchbackend.services.interfaces.AdministradorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/privado")
@RequiredArgsConstructor
public class AdminController {

   //private final AdministradorService administradorService;

  /*  @PostMapping("/registrar/{correo}/{password}")
    public ResponseEntity<AuthResponseDTO> registrar(@PathVariable String correo, @PathVariable String password) {
        AdministradorEntidad usuario = administradorService.crearUsuario(correo, password);
        String token = administradorService.validarCredenciales(correo, password);
        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setCorreo(usuario.getCorreo());
        return ResponseEntity.ok(response);
    }
*/

}
