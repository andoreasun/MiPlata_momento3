package com.miplata.application.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class TransferenciaResponse {
    private String cuentaOrigen;
    private String cuentaDestino;
    private BigDecimal monto;
    private BigDecimal saldoOrigenResultante;
}