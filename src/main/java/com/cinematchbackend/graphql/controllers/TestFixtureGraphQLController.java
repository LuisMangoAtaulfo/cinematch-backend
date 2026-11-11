package com.cinematchbackend.graphql.controllers;

import com.cinematchbackend.dto.request.ContenidoTestRequestDTO;
import com.cinematchbackend.dto.response.TestSalaPayloadDTO;
import com.cinematchbackend.exceptions.TestModeNotEnabledException;
import com.cinematchbackend.services.interfaces.SimulacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TestFixtureGraphQLController {

    private final SimulacionService simulacionService;

    @Value("${app.test-mode:false}")
    private boolean testMode;

    // ── Fixtures ──────────────────────────────────────────────

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public TestSalaPayloadDTO crearSalaTest(@Argument Long usuario1Id, @Argument Long usuario2Id, @Argument String estado) {
        verificarTestMode();
        log.info("[TEST] crearSalaTest usuario1={} usuario2={} estado={}", usuario1Id, usuario2Id, estado);
        return simulacionService.crearSalaTest(usuario1Id, usuario2Id, estado);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean unirUsuarioTest(@Argument String codigo, @Argument Long usuarioId) {
        verificarTestMode();
        simulacionService.unirUsuarioTest(codigo, usuarioId);
        return true;
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean finalizarSalaTest(@Argument String codigo) {
        verificarTestMode();
        simulacionService.finalizarSalaTest(codigo);
        return true;
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean limpiarDatosTest() {
        verificarTestMode();
        simulacionService.limpiarDatosTest();
        return true;
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean insertarContenidoTest(@Argument ContenidoTestRequestDTO contenido) {
        verificarTestMode();
        simulacionService.insertarContenidoTest(contenido);
        return true;
    }

    // ── Simulaciones ──────────────────────────────────────────

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean simularEvaluacion(@Argument Long salaId, @Argument Long usuarioId,
                                     @Argument String contenidoId, @Argument Boolean evaluacion) {
        verificarTestMode();
        simulacionService.simularEvaluacion(salaId, usuarioId, contenidoId, evaluacion);
        return true;
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean simularMensaje(@Argument Long salaId, @Argument Long usuarioId, @Argument String texto) {
        verificarTestMode();
        simulacionService.simularMensaje(salaId, usuarioId, texto);
        return true;
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean simularUnion(@Argument String salaCodigo, @Argument Long usuarioId) {
        verificarTestMode();
        simulacionService.simularUnion(salaCodigo, usuarioId);
        return true;
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean simularDesconexion(@Argument Long salaId, @Argument Long usuarioId) {
        verificarTestMode();
        simulacionService.simularDesconexion(salaId, usuarioId);
        return true;
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public Boolean simularReconexion(@Argument Long salaId, @Argument Long usuarioId) {
        verificarTestMode();
        simulacionService.simularReconexion(salaId, usuarioId);
        return true;
    }

    // ── Helper ────────────────────────────────────────────────

    private void verificarTestMode() {
        if (!testMode) {
            throw new TestModeNotEnabledException(
                    "Los endpoints de test solo están disponibles con TEST_MODE=true");
        }
    }
}
