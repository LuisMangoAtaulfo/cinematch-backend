package com.cinematchbackend.exceptions;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(String mensaje) {
        super(mensaje);
    }
}