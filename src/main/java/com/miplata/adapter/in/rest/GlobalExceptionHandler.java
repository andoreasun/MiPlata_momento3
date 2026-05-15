package com.miplata.adapter.in.rest;

import com.miplata.domain.exception.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<Map<String, String>> saldo(
            SaldoInsuficienteException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("error", "SALDO_INSUFICIENTE",
                        "mensaje", e.getMessage()));
    }

    @ExceptionHandler(CuentaBloqueadaException.class)
    public ResponseEntity<Map<String, String>> bloqueada(
            CuentaBloqueadaException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "CUENTA_BLOQUEADA",
                        "mensaje", e.getMessage()));
    }

    @ExceptionHandler(TransferenciaInvalidaException.class)
    public ResponseEntity<Map<String, String>> transferencia(
            TransferenciaInvalidaException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "TRANSFERENCIA_INVALIDA",
                        "mensaje", e.getMessage()));
    }

    @ExceptionHandler(MontoInvalidoException.class)
    public ResponseEntity<Map<String, String>> monto(
            MontoInvalidoException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "MONTO_INVALIDO",
                        "mensaje", e.getMessage()));
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, String>> credenciales(
            CredencialesInvalidasException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "CREDENCIALES_INVALIDAS",
                        "mensaje", e.getMessage()));
    }

    @ExceptionHandler(CupoExcedidoException.class)
    public ResponseEntity<Map<String, String>> cupo(
            CupoExcedidoException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("error", "CUPO_EXCEDIDO",
                        "mensaje", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> general(
            IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "ARGUMENTO_INVALIDO",
                        "mensaje", e.getMessage()));
    }
}