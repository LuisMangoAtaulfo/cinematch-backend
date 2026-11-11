package com.cinematchbackend.exceptions;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String mensaje) {
        super(mensaje);
    }
}
