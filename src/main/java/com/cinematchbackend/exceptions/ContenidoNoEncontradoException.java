package com.cinematchbackend.exceptions;

public class ContenidoNoEncontradoException extends RuntimeException {
    public ContenidoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}