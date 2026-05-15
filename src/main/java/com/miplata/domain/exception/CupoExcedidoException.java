package com.miplata.domain.exception;

public class CupoExcedidoException extends RuntimeException {
    public CupoExcedidoException(String mensaje) {
        super(mensaje);
    }
}