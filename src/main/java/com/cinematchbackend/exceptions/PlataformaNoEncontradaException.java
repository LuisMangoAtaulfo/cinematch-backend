package com.cinematchbackend.exceptions;

public class PlataformaNoEncontradaException extends RuntimeException {
    public PlataformaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}