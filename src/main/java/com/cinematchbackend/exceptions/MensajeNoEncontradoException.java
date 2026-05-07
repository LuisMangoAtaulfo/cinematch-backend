package com.cinematchbackend.exceptions;

public class MensajeNoEncontradoException extends RuntimeException {
    public MensajeNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}