package com.cinematchbackend.exceptions;

public class CamposVaciosException extends RuntimeException {
    public CamposVaciosException() {
        super("Completa todos los campos");
    }

    public CamposVaciosException(String mensaje) {
        super(mensaje);
    }
}
