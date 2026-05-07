package com.cinematchbackend.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String mensaje) {
        super(mensaje);
    }
}