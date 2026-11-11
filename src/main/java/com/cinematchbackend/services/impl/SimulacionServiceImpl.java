package com.cinematchbackend.services.impl;

import com.cinematchbackend.dto.request.ContenidoTestRequestDTO;
import com.cinematchbackend.dto.request.EvaluacionRequestDTO;
import com.cinematchbackend.dto.request.MensajeRequestDTO;
import com.cinematchbackend.dto.request.UnirseSalaDTO;
import com.cinematchbackend.dto.response.EstadoCompaneroDTO;
import com.cinematchbackend.dto.response.TestSalaPayloadDTO;
import com.cinematchbackend.entities.*;
import com.cinematchbackend.enums.EstadoSala;
import com.cinematchbackend.exceptions.CodigoSalaInvalidoException;
import com.cinematchbackend.exceptions.UsuarioNoEncontradoException;
import com.cinematchbackend.repositories.*;
import com.cinematchbackend.services.interfaces.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimulacionServiceImpl implements SimulacionService {

    private static final String ALFANUMERICO = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LONGITUD_CODIGO = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final SalaRepository salaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ContenidoRepository contenidoRepository;
    private final TestSimulacionRepository testSimulacionRepository;
    private final EvaluacionService evaluacionService;
    private final ChatService chatService;
    private final EventPublisherService eventPublisherService;
    private final ConexionService conexionService;

    @Override
    @Transactional
    public TestSalaPayloadDTO crearSalaTest(Long usuario1Id, Long usuario2Id, String estado) {
        UsuarioEntidad usuario1 = usuarioRepository.findById(usuario1Id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario 1 no encontrado: " + usuario1Id));

        UsuarioEntidad usuario2 = null;
        if (usuario2Id != null) {
            usuario2 = usuarioRepository.findById(usuario2Id)
                    .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario 2 no encontrado: " + usuario2Id));
        }

        EstadoSala estadoSala = parseEstado(estado, usuario2 != null);

        String codigo = generarCodigoUnico();
        SalaEntidad sala = new SalaEntidad.Builder()
                .codigo(codigo)
                .estado(estadoSala)
                .usuario1(usuario1)
                .usuario2(usuario2)
                .build();

        SalaEntidad saved = salaRepository.save(sala);

        // Registrar conexiones si la sala está activa
        if (estadoSala == EstadoSala.ACTIVA) {
            conexionService.registrarConexion(saved.getId(), usuario1.getId());
            if (usuario2 != null) {
                conexionService.registrarConexion(saved.getId(), usuario2.getId());
            }
        }

        log.info("[TEST] Sala creada: {} estado={}", codigo, estadoSala);

        TestSalaPayloadDTO payload = new TestSalaPayloadDTO();
        payload.setCodigo(saved.getCodigo());
        payload.setSalaId(saved.getId());
        return payload;
    }

    @Override
    @Transactional
    public void unirUsuarioTest(String codigo, Long usuarioId) {
        SalaEntidad sala = salaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new CodigoSalaInvalidoException("Código de sala inválido: " + codigo));

        UsuarioEntidad usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + usuarioId));

        sala.setUsuario2(usuario);
        sala.setEstado(EstadoSala.ACTIVA);
        salaRepository.save(sala);

        conexionService.registrarConexion(sala.getId(), usuarioId);
        log.info("[TEST] Usuario {} unido a sala {}", usuarioId, codigo);
    }

    @Override
    @Transactional
    public void finalizarSalaTest(String codigo) {
        SalaEntidad sala = salaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new CodigoSalaInvalidoException("Código de sala inválido: " + codigo));

        sala.setEstado(EstadoSala.FINALIZADA);
        salaRepository.save(sala);
        log.info("[TEST] Sala {} finalizada", codigo);
    }

    @Override
    @Transactional
    public void limpiarDatosTest() {
        // Eliminar todas las salas (cascade eliminará matches, mensajes, evaluaciones, filtros)
        salaRepository.deleteAll();
        log.info("[TEST] Datos de prueba limpiados");
    }

    @Override
    @Transactional
    public void insertarContenidoTest(ContenidoTestRequestDTO dto) {
        ContenidoEntidad contenido = new ContenidoEntidad();
        contenido.setContenidoId(dto.getContenidoId());
        contenido.setTitulo(dto.getTitulo());
        contenido.setAnio(dto.getAnio());
        contenido.setTipo(dto.getTipo());
        contenido.setGenero(dto.getGenero());
        contenido.setImagen(dto.getImagen());
        contenido.setSinopsis(dto.getSinopsis());
        contenidoRepository.save(contenido);
        log.info("[TEST] Contenido insertado: {}", dto.getContenidoId());
    }

    @Override
    public void simularEvaluacion(Long salaId, Long usuarioId, String contenidoId, Boolean evaluacion) {
        EvaluacionRequestDTO dto = new EvaluacionRequestDTO();
        dto.setSalaId(salaId);
        dto.setUsuarioId(usuarioId);
        dto.setContenidoId(contenidoId);
        dto.setDecision(evaluacion);

        evaluacionService.registrarEvaluacion(dto);
        registrarSimulacion("EVALUACION", salaId, usuarioId,
                "contenido=" + contenidoId + " decision=" + evaluacion);
        log.info("[TEST] Evaluación simulada: usuario={} sala={} contenido={} decision={}",
                usuarioId, salaId, contenidoId, evaluacion);
    }

    @Override
    public void simularMensaje(Long salaId, Long usuarioId, String texto) {
        MensajeRequestDTO dto = new MensajeRequestDTO();
        dto.setSalaId(salaId);
        dto.setUsuarioId(usuarioId);
        dto.setTexto(texto);

        chatService.procesarMensaje(dto);
        registrarSimulacion("MENSAJE", salaId, usuarioId, "texto=" + texto);
        log.info("[TEST] Mensaje simulado: usuario={} sala={} texto={}", usuarioId, salaId, texto);
    }

    @Override
    public void simularUnion(String salaCodigo, Long usuarioId) {
        SalaEntidad sala = salaRepository.findByCodigo(salaCodigo)
                .orElseThrow(() -> new CodigoSalaInvalidoException("Código de sala inválido: " + salaCodigo));

        UnirseSalaDTO dto = new UnirseSalaDTO();
        dto.setCodigo(sala.getCodigo());
        dto.setUsuarioId(usuarioId);

        UsuarioEntidad usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + usuarioId));

        sala.setUsuario2(usuario);
        sala.setEstado(EstadoSala.ACTIVA);
        salaRepository.save(sala);

        conexionService.registrarConexion(sala.getId(), usuarioId);
        registrarSimulacion("UNION", sala.getId(), usuarioId, "codigo=" + salaCodigo);
        log.info("[TEST] Unión simulada: usuario={} sala={}", usuarioId, salaCodigo);
    }

    @Override
    public void simularDesconexion(Long salaId, Long usuarioId) {
        conexionService.registrarDesconexion(salaId, usuarioId);

        EstadoCompaneroDTO estado = new EstadoCompaneroDTO();
        estado.setUsuarioId(usuarioId);
        estado.setEstado("DESCONECTADO");
        usuarioRepository.findById(usuarioId)
                .ifPresent(u -> estado.setNombreUsuario(u.getNombre()));
        eventPublisherService.publicarEstadoCompanero(salaId, estado);

        registrarSimulacion("DESCONEXION", salaId, usuarioId, null);
        log.info("[TEST] Desconexión simulada: usuario={} sala={}", usuarioId, salaId);
    }

    @Override
    public void simularReconexion(Long salaId, Long usuarioId) {
        conexionService.registrarConexion(salaId, usuarioId);

        EstadoCompaneroDTO estado = new EstadoCompaneroDTO();
        estado.setUsuarioId(usuarioId);
        estado.setEstado("RECONECTADO");
        usuarioRepository.findById(usuarioId)
                .ifPresent(u -> estado.setNombreUsuario(u.getNombre()));
        eventPublisherService.publicarEstadoCompanero(salaId, estado);

        registrarSimulacion("RECONEXION", salaId, usuarioId, null);
        log.info("[TEST] Reconexión simulada: usuario={} sala={}", usuarioId, salaId);
    }

    // ── Helpers ──────────────────────────────────────────────

    private void registrarSimulacion(String tipo, Long salaId, Long usuarioId, String detalle) {
        TestSimulacionEntidad sim = new TestSimulacionEntidad();
        sim.setTipoSimulacion(tipo);
        sim.setSalaId(salaId);
        sim.setUsuarioId(usuarioId);
        sim.setDetalle(detalle);
        testSimulacionRepository.save(sim);
    }

    private EstadoSala parseEstado(String estado, boolean tieneInvitado) {
        if (estado == null || estado.isBlank()) {
            return tieneInvitado ? EstadoSala.ACTIVA : EstadoSala.CREADA;
        }
        try {
            return EstadoSala.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            return tieneInvitado ? EstadoSala.ACTIVA : EstadoSala.CREADA;
        }
    }

    private String generarCodigoUnico() {
        String codigo;
        do {
            StringBuilder sb = new StringBuilder(LONGITUD_CODIGO);
            for (int i = 0; i < LONGITUD_CODIGO; i++) {
                sb.append(ALFANUMERICO.charAt(RANDOM.nextInt(ALFANUMERICO.length())));
            }
            codigo = sb.toString();
        } while (salaRepository.findByCodigo(codigo).isPresent());
        return codigo;
    }
}
