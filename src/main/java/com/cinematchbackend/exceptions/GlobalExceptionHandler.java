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
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
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
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleTokenInvalido(TokenInvalidoException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
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