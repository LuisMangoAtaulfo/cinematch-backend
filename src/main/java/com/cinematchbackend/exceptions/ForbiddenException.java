package com.cinematchbackend.exceptions;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
        super("No tienes permisos para realizar esta acción");
    }

    public ForbiddenException(String mensaje) {
        super(mensaje);
    }
}
