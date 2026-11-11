package com.cinematchbackend.exceptions;

public class NombreRequeridoException extends RuntimeException {
    public NombreRequeridoException() {
        super("El nombre es requerido");
    }

    public NombreRequeridoException(String mensaje) {
        super(mensaje);
    }
}
