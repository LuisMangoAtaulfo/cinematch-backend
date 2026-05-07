package com.cinematchbackend.exceptions;

public class EvaluacionDuplicadaException extends RuntimeException {
    public EvaluacionDuplicadaException(String mensaje) {
        super(mensaje);
    }
}