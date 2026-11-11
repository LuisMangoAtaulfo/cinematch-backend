package com.cinematchbackend.services.impl;

import com.cinematchbackend.dto.request.SalaRequestDTO;
import com.cinematchbackend.dto.request.UnirseSalaDTO;
import com.cinematchbackend.dto.response.*;
import com.cinematchbackend.entities.SalaEntidad;
import com.cinematchbackend.entities.UsuarioEntidad;
import com.cinematchbackend.enums.EstadoSala;
import com.cinematchbackend.exceptions.AuthorizationException;
import com.cinematchbackend.exceptions.CodigoSalaInvalidoException;
import com.cinematchbackend.exceptions.SalaActivaException;
import com.cinematchbackend.exceptions.SalaNoEncontradaException;
import com.cinematchbackend.exceptions.UsuarioNoEncontradoException;
import com.cinematchbackend.exceptions.ValidationException;
import com.cinematchbackend.repositories.*;
import com.cinematchbackend.services.interfaces.ConexionService;
import com.cinematchbackend.services.interfaces.EventPublisherService;
import com.cinematchbackend.services.interfaces.SalaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalaServiceImpl implements SalaService {

    private static final String ALFANUMERICO = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LONGITUD_CODIGO = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final SalaRepository salaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MatchRepository matchRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MensajeRepository mensajeRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final EventPublisherService eventPublisherService;
    private final ConexionService conexionService;

    @Override
    public SalaResponseDTO crearSala(SalaRequestDTO dto) {
        UsuarioEntidad usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + dto.getUsuarioId()));

        SalaEntidad sala = new SalaEntidad.Builder()
                .codigo(generarCodigoUnico())
                .estado(EstadoSala.CREADA)
                .usuario1(usuario)
                .build();

        SalaEntidad saved = salaRepository.save(sala);
        log.info("Sala creada: {} (estado: CREADA)", saved.getCodigo());

        // Notificar cambio de estado
        publicarCambio(saved.getId(), EstadoSala.CREADA, "Sala creada");

        return mapearAResponse(saved);
    }

    @Override
    public SalaResponseDTO unirseASala(UnirseSalaDTO dto) {
        if (dto.getCodigo() == null || dto.getCodigo().isBlank()) {
            throw new ValidationException("Ingresa un código de sala");
        }

        SalaEntidad sala = salaRepository.findByCodigo(dto.getCodigo().toUpperCase().trim())
                .orElseThrow(() -> new CodigoSalaInvalidoException("Código de sala inválido: " + dto.getCodigo()));

        // Permitir unirse desde CREADA o ESPERANDO
        if (sala.getEstado() != EstadoSala.CREADA && sala.getEstado() != EstadoSala.ESPERANDO) {
            if (sala.getEstado() == EstadoSala.FINALIZADA || sala.getEstado() == EstadoSala.CANCELADA) {
                throw new SalaActivaException("La sala ya no está disponible");
            }
            throw new SalaActivaException("La sala ya está activa o finalizada");
        }

        UsuarioEntidad usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado: " + dto.getUsuarioId()));

        sala.setUsuario2(usuario);
        sala.setEstado(EstadoSala.ACTIVA);

        SalaEntidad saved = salaRepository.save(sala);
        log.info("Sala {} → ACTIVA (usuario {} se unió)", saved.getCodigo(), usuario.getId());

        // Registrar conexiones
        conexionService.registrarConexion(saved.getId(), sala.getUsuario1().getId());
        conexionService.registrarConexion(saved.getId(), usuario.getId());

        publicarCambio(saved.getId(), EstadoSala.ACTIVA, "Compañero se unió a la sala");

        return mapearAResponse(saved);
    }

    @Override
    public SalaResponseDTO obtenerSala(Long salaId) {
        SalaEntidad sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new SalaNoEncontradaException("Sala no encontrada: " + salaId));
        return mapearAResponse(sala);
    }

    @Override
    public SalaResponseDTO buscarSalaPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new ValidationException("Ingresa un código de sala");
        }
        SalaEntidad sala = salaRepository.findByCodigo(codigo.toUpperCase().trim())
                .orElseThrow(() -> new CodigoSalaInvalidoException("Código de sala inválido: " + codigo));
        return mapearAResponse(sala);
    }

    @Override
    public void finalizarSala(Long salaId, Long usuarioId) {
        SalaEntidad sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new SalaNoEncontradaException("Sala no encontrada: " + salaId));

        // Solo el creador puede finalizar
        if (sala.getUsuario1() == null || !sala.getUsuario1().getId().equals(usuarioId)) {
            throw new AuthorizationException("Solo el creador puede finalizar la sala");
        }

        sala.setEstado(EstadoSala.FINALIZADA);
        salaRepository.save(sala);

        messagingTemplate.convertAndSend("/topic/sala/" + salaId + "/finalizar", true);
        eventPublisherService.publicarSalaFinalizada(salaId);
        publicarCambio(salaId, EstadoSala.FINALIZADA, "Sala finalizada");

        log.info("Sala {} finalizada por usuario {}", salaId, usuarioId);
    }

    @Override
    public void cancelarSala(Long salaId, Long usuarioId) {
        SalaEntidad sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new SalaNoEncontradaException("Sala no encontrada: " + salaId));

        if (sala.getEstado() == EstadoSala.FINALIZADA || sala.getEstado() == EstadoSala.CANCELADA) {
            throw new SalaActivaException("No se puede cancelar una sala en estado: " + sala.getEstado());
        }

        boolean esCreador = sala.getUsuario1() != null && sala.getUsuario1().getId().equals(usuarioId);
        boolean esInvitado = sala.getUsuario2() != null && sala.getUsuario2().getId().equals(usuarioId);

        if (!esCreador && !esInvitado) {
            throw new AuthorizationException("No tienes permiso para cancelar esta sala");
        }

        // Solo el creador puede cancelar una sala activa
        if (sala.getEstado() == EstadoSala.ACTIVA && !esCreador) {
            throw new AuthorizationException("Solo el creador puede cancelar una sala activa");
        }

        sala.setEstado(EstadoSala.CANCELADA);
        salaRepository.save(sala);
        publicarCambio(salaId, EstadoSala.CANCELADA, "Sala cancelada");
        log.info("Sala {} cancelada por usuario {}", salaId, usuarioId);
    }

    @Override
    public void eliminarDatosSala(Long salaId) {
        SalaEntidad sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new SalaNoEncontradaException("Sala no encontrada: " + salaId));
        conexionService.limpiarEstados(salaId);
        salaRepository.delete(sala);
    }

    @Override
    public List<MatchResponseDTO> obtenerMatches(Long salaId) {
        return matchRepository.findBySalaId(salaId).stream()
                .map(match -> {
                    MatchResponseDTO dto = new MatchResponseDTO();
                    dto.setId(match.getId());
                    dto.setFechaDeteccion(match.getFechaDeteccion());
                    dto.setSalaId(salaId);
                    return dto;
                }).toList();
    }

    @Override
    public ResultadoSalaDTO obtenerResultados(Long salaId) {
        SalaEntidad sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new SalaNoEncontradaException("Sala no encontrada: " + salaId));

        List<MatchResponseDTO> matches = matchRepository.findBySalaId(salaId).stream()
                .map(match -> {
                    MatchResponseDTO dto = new MatchResponseDTO();
                    dto.setId(match.getId());
                    dto.setFechaDeteccion(match.getFechaDeteccion());
                    dto.setSalaId(salaId);
                    if (match.getContenido() != null) {
                        ContenidoResponseDTO contenidoDTO = new ContenidoResponseDTO();
                        contenidoDTO.setContenidoId(match.getContenido().getContenidoId());
                        contenidoDTO.setTitulo(match.getContenido().getTitulo());
                        contenidoDTO.setAnio(match.getContenido().getAnio());
                        contenidoDTO.setTipo(match.getContenido().getTipo());
                        contenidoDTO.setGenero(match.getContenido().getGenero());
                        contenidoDTO.setImagen(match.getContenido().getImagen());
                        contenidoDTO.setSinopsis(match.getContenido().getSinopsis());
                        contenidoDTO.setPlataformas(
                                match.getContenido().getPlataformas().stream()
                                        .map(cp -> cp.getPlataforma().getNombre())
                                        .toList()
                        );
                        dto.setContenido(contenidoDTO);
                    }
                    return dto;
                }).toList();

        ResultadoSalaDTO resultado = new ResultadoSalaDTO();
        resultado.setSalaId(salaId);
        resultado.setMatches(matches);
        resultado.setTotalMatches(matches.size());
        resultado.setTotalEvaluaciones(evaluacionRepository.findBySalaId(salaId).size());
        resultado.setTotalMensajes(mensajeRepository.findBySalaIdOrderByFechaEnvioAsc(salaId).size());
        return resultado;
    }

    @Override
    public EstadoSalaCompletaDTO obtenerEstadoSala(Long salaId) {
        SalaEntidad sala = salaRepository.findById(salaId)
                .orElseThrow(() -> new SalaNoEncontradaException("Sala no encontrada: " + salaId));

        EstadoSalaCompletaDTO estado = new EstadoSalaCompletaDTO();
        estado.setSalaId(sala.getId());
        estado.setCodigo(sala.getCodigo());
        estado.setEstado(sala.getEstado());
        estado.setEstadosUsuarios(conexionService.obtenerEstados(salaId));
        return estado;
    }

    private void publicarCambio(Long salaId, EstadoSala estado, String mensaje) {
        CambioSalaDTO cambio = new CambioSalaDTO();
        cambio.setSalaId(salaId);
        cambio.setEstado(estado);
        cambio.setMensaje(mensaje);
        eventPublisherService.publicarCambioSala(salaId, cambio);
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

    private SalaResponseDTO mapearAResponse(SalaEntidad sala) {
        SalaResponseDTO response = new SalaResponseDTO();
        response.setId(sala.getId());
        response.setCodigo(sala.getCodigo());
        response.setEstado(sala.getEstado());
        response.setFechaCreacion(sala.getFechaCreacion());
        if (sala.getUsuario1() != null) response.setUsuario1(mapearUsuario(sala.getUsuario1()));
        if (sala.getUsuario2() != null) response.setUsuario2(mapearUsuario(sala.getUsuario2()));
        return response;
    }

    private UsuarioResponseDTO mapearUsuario(UsuarioEntidad usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setCorreo(usuario.getCorreo());
        dto.setDescripcion(usuario.getDescripcion());
        dto.setRol(usuario.getRol());
        return dto;
    }
}
