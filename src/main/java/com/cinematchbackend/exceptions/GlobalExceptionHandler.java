package com.cinematchbackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioNoEncontrado(UsuarioNoEncontradoException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(SalaNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleSalaNoEncontrada(SalaNoEncontradaException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CodigoSalaInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleCodigoSalaInvalido(CodigoSalaInvalidoException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Código de sala inválido");
    }

    @ExceptionHandler(SalaActivaException.class)
    public ResponseEntity<Map<String, Object>> handleSalaActiva(SalaActivaException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ContenidoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleContenidoNoEncontrado(ContenidoNoEncontradoException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(EvaluacionDuplicadaException.class)
    public ResponseEntity<Map<String, Object>> handleEvaluacionDuplicada(EvaluacionDuplicadaException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MatchNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleMatchNoEncontrado(MatchNoEncontradoException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(PlataformaNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handlePlataformaNoEncontrada(PlataformaNoEncontradaException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MensajeNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleMensajeNoEncontrado(MensajeNoEncontradoException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleTokenInvalido(TokenInvalidoException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthorization(AuthorizationException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(ValidationException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(CalificacionInvalidaException.class)
    public ResponseEntity<Map<String, Object>> handleCalificacionInvalida(CalificacionInvalidaException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(CalificacionDuplicadaException.class)
    public ResponseEntity<Map<String, Object>> handleCalificacionDuplicada(CalificacionDuplicadaException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(SalaNoFinalizadaException.class)
    public ResponseEntity<Map<String, Object>> handleSalaNoFinalizada(SalaNoFinalizadaException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(UsuarioDesconectadoException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioDesconectado(UsuarioDesconectadoException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(TestModeNotEnabledException.class)
    public ResponseEntity<Map<String, Object>> handleTestMode(TestModeNotEnabledException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // R-BE-01: Nuevas excepciones de validación
    @ExceptionHandler(CamposVaciosException.class)
    public ResponseEntity<Map<String, Object>> handleCamposVacios(CamposVaciosException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Completa todos los campos");
    }

    @ExceptionHandler(NombreRequeridoException.class)
    public ResponseEntity<Map<String, Object>> handleNombreRequerido(NombreRequeridoException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "El nombre es requerido");
    }

    // R-BE-02/03: Excepciones de seguridad
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Autenticación requerida");
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(ForbiddenException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "No tienes permisos para realizar esta acción");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "mensaje", mensaje
        ));
    }
}
