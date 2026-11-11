package com.cinematchbackend.exceptions;

public class SalaNoFinalizadaException extends RuntimeException {
    public SalaNoFinalizadaException(String mensaje) {
        super(mensaje);
    }
}
