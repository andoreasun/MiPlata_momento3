package com.miplata.application.dto;

import com.miplata.domain.enums.EstadoCuenta;
import com.miplata.domain.enums.TipoCuenta;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class CuentaResponse {
    private String id;
    private String numeroCuenta;
    private TipoCuenta tipoCuenta;
    private BigDecimal saldo;
    private EstadoCuenta estado;
    private BigDecimal cupoDisponible;
}