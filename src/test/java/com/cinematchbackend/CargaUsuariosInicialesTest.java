package com.cinematchbackend;

import com.cinematchbackend.dto.request.UsuarioRegistroDTO;
import com.cinematchbackend.repositories.AdministradorRepository;
import com.cinematchbackend.repositories.UsuarioRepository;
import com.cinematchbackend.services.interfaces.AdministradorService;
import com.cinematchbackend.services.interfaces.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Crea (o verifica) los usuarios iniciales de desarrollo usando los
 * services reales del backend, de modo que las contraseñas se guardan
 * con el PasswordEncoder (BCrypt) de la aplicación.
 *
 * Es idempotente: si el correo ya existe no vuelve a insertar.
 */
@SpringBootTest
class CargaUsuariosInicialesTest {

    private static final String CORREO_ADMIN = "mango@admin.com";
    private static final String PASSWORD_ADMIN = "12345678#";
    private static final String CORREO_USUARIO = "mango@mail.com";
    private static final String PASSWORD_USUARIO = "12345678";

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private AdministradorService administradorService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AdministradorRepository administradorRepository;

    @Test
    void crearUsuariosIniciales() {
        if (!administradorRepository.existsByCorreo(CORREO_ADMIN)) {
            administradorService.crearUsuario(CORREO_ADMIN, PASSWORD_ADMIN);
        }

        if (!usuarioRepository.existsByCorreo(CORREO_USUARIO)) {
            UsuarioRegistroDTO dto = new UsuarioRegistroDTO();
            dto.setNombre("Mango");
            dto.setCorreo(CORREO_USUARIO);
            dto.setPassword(PASSWORD_USUARIO);
            usuarioService.crearUsuario(dto);
        }

        assertTrue(usuarioRepository.existsByCorreo(CORREO_USUARIO));
        assertTrue(administradorRepository.existsByCorreo(CORREO_ADMIN));

        // La validación compara con BCrypt y emite el JWT: demuestra que
        // las credenciales guardadas funcionan de extremo a extremo.
        assertNotNull(usuarioService.validarCredenciales(CORREO_USUARIO, PASSWORD_USUARIO));
        assertNotNull(administradorService.validarCredenciales(CORREO_ADMIN, PASSWORD_ADMIN));
    }
}
