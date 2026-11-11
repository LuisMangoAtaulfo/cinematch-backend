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
 * Crea (o verifica) los usuarios de prueba que necesitan los tests
 * automatizados de Robot Framework (test_*.robot).
 *
 * Usuarios de prueba:
 *   - test1@cinematch.com / Test1234       (usuario normal)
 *   - test2@cinematch.com / Test1234       (usuario normal)
 *   - admin@cinematch.com / Admin1234      (administrador)
 *
 * Es idempotente: si el correo ya existe no vuelve a insertar.
 */
@SpringBootTest
class CargaUsuariosRobotTest {

    private static final String CORREO_TEST1 = "test1@cinematch.com";
    private static final String PASSWORD_TEST1 = "Test1234";
    private static final String NOMBRE_TEST1 = "Usuario Test 1";

    private static final String CORREO_TEST2 = "test2@cinematch.com";
    private static final String PASSWORD_TEST2 = "Test1234";
    private static final String NOMBRE_TEST2 = "Usuario Test 2";

    private static final String CORREO_ADMIN = "admin@cinematch.com";
    private static final String PASSWORD_ADMIN = "Admin1234";

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private AdministradorService administradorService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AdministradorRepository administradorRepository;

    @Test
    void crearUsuariosDePruebaRobot() {
        // Crear usuario test1
        if (!usuarioRepository.existsByCorreo(CORREO_TEST1)) {
            UsuarioRegistroDTO dto1 = new UsuarioRegistroDTO();
            dto1.setNombre(NOMBRE_TEST1);
            dto1.setCorreo(CORREO_TEST1);
            dto1.setPassword(PASSWORD_TEST1);
            usuarioService.crearUsuario(dto1);
        }

        // Crear usuario test2
        if (!usuarioRepository.existsByCorreo(CORREO_TEST2)) {
            UsuarioRegistroDTO dto2 = new UsuarioRegistroDTO();
            dto2.setNombre(NOMBRE_TEST2);
            dto2.setCorreo(CORREO_TEST2);
            dto2.setPassword(PASSWORD_TEST2);
            usuarioService.crearUsuario(dto2);
        }

        // Crear admin
        if (!administradorRepository.existsByCorreo(CORREO_ADMIN)) {
            administradorService.crearUsuario(CORREO_ADMIN, PASSWORD_ADMIN);
        }

        // Verificar que todos existen
        assertTrue(usuarioRepository.existsByCorreo(CORREO_TEST1));
        assertTrue(usuarioRepository.existsByCorreo(CORREO_TEST2));
        assertTrue(administradorRepository.existsByCorreo(CORREO_ADMIN));

        // Verificar que las credenciales funcionan
        assertNotNull(usuarioService.validarCredenciales(CORREO_TEST1, PASSWORD_TEST1));
        assertNotNull(usuarioService.validarCredenciales(CORREO_TEST2, PASSWORD_TEST2));
        assertNotNull(administradorService.validarCredenciales(CORREO_ADMIN, PASSWORD_ADMIN));

        System.out.println("✓ Usuarios de prueba creados correctamente:");
        System.out.println("  - test1@cinematch.com / Test1234");
        System.out.println("  - test2@cinematch.com / Test1234");
        System.out.println("  - admin@cinematch.com / Admin1234");
    }
}
