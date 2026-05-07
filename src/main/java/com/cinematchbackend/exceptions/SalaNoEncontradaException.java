package com.cinematchbackend.exceptions;

public class SalaNoEncontradaException extends RuntimeException {
    public SalaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}