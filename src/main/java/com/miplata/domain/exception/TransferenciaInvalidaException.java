package com.miplata.domain.exception;

public class TransferenciaInvalidaException extends RuntimeException {
    public TransferenciaInvalidaException(String mensaje) {
        super(mensaje);
    }
}