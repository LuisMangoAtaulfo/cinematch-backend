package com.cinematchbackend.exceptions;

public class MatchNoEncontradoException extends RuntimeException {
    public MatchNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}