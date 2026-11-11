package com.cinematchbackend.services.interfaces;

import com.cinematchbackend.dto.request.ContenidoTestRequestDTO;
import com.cinematchbackend.dto.response.TestSalaPayloadDTO;

/**
 * Endpoints de prueba para automatización de Robot Framework (R-BE-09).
 * Solo disponibles cuando TEST_MODE=true.
 */
public interface SimulacionService {
    TestSalaPayloadDTO crearSalaTest(Long usuario1Id, Long usuario2Id, String estado);
    void unirUsuarioTest(String codigo, Long usuarioId);
    void finalizarSalaTest(String codigo);
    void limpiarDatosTest();
    void insertarContenidoTest(ContenidoTestRequestDTO dto);

    void simularEvaluacion(Long salaId, Long usuarioId, String contenidoId, Boolean evaluacion);
    void simularMensaje(Long salaId, Long usuarioId, String texto);
    void simularUnion(String salaCodigo, Long usuarioId);
    void simularDesconexion(Long salaId, Long usuarioId);
    void simularReconexion(Long salaId, Long usuarioId);
}
