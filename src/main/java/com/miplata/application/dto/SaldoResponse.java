package com.miplata.application.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class SaldoResponse {
    private String numeroCuenta;
    private BigDecimal saldo;
    private String tipoCuenta;
}