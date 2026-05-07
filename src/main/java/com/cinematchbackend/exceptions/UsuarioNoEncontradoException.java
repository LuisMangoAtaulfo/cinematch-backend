package com.cinematchbackend.exceptions;

public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}