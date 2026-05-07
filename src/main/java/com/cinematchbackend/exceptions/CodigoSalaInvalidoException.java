package com.cinematchbackend.exceptions;

public class CodigoSalaInvalidoException extends RuntimeException {
    public CodigoSalaInvalidoException(String mensaje) {
        super(mensaje);
    }
}