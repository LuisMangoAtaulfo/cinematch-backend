package com.cinematchbackend.exceptions;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Autenticación requerida");
    }

    public UnauthorizedException(String mensaje) {
        super(mensaje);
    }
}
