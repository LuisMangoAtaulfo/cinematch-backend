package com.cinematchbackend.exceptions;

public class UsuarioDesconectadoException extends RuntimeException {
    public UsuarioDesconectadoException(String mensaje) {
        super(mensaje);
    }
}
