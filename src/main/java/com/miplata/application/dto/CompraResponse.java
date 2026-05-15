package com.miplata.application.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class CompraResponse {
    private BigDecimal montoCompra;
    private int cuotas;
    private BigDecimal cuotaMensual;
    private BigDecimal cupoDisponibleResultante;
    private String resumen;
}