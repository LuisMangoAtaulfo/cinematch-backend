package com.cinematchbackend.exceptions;

public class TestModeNotEnabledException extends RuntimeException {
    public TestModeNotEnabledException(String mensaje) {
        super(mensaje);
    }
}
