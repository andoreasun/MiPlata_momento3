package com.miplata.application.dto;

import com.miplata.domain.enums.TipoCuenta;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AbrirCuentaRequest {
    @NotNull private TipoCuenta tipoCuenta;
    @Positive private BigDecimal saldoInicial;
    @Positive private BigDecimal cupoCredito;
}