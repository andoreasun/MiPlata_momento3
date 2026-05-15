package com.miplata.application.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class RetiroResponse {
    private BigDecimal montoRetirado;
    private BigDecimal interesAplicado;
    private BigDecimal saldoResultante;
}