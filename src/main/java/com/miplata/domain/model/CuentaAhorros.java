package com.miplata.domain.model;

import com.miplata.domain.enums.TipoCuenta;
import com.miplata.domain.enums.TipoMovimiento;
import com.miplata.domain.exception.SaldoInsuficienteException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * EPI 1 – Motor lógico POO | MP-2: CuentaAhorros – reglas de interés al retiro
 *
 * Como: cliente con cuenta de ahorros.
 * Quiero: que al retirar dinero se aplique automáticamente el 1.5% de interés mensual.
 * Para: que el sistema respete las reglas del producto.
 *
 * Criterios de aceptación:
 * - Override de retirar() aplica 1.5% de interés sobre el monto retirado
 * - No permite retiros que superen el saldo disponible (monto + interés)
 * - El movimiento registrado refleja el monto con interés aplicado
 * - No permite sobregiro bajo ninguna condición
 */
public class CuentaAhorros extends Cuenta {

    private static final BigDecimal TASA_INTERES = new BigDecimal("0.015");

    public CuentaAhorros(UUID id, String numeroCuenta,
                         BigDecimal saldoInicial, UUID clienteId) {
        super(id, numeroCuenta, TipoCuenta.AHORROS, saldoInicial, clienteId);
    }

    @Override
    public BigDecimal retirar(BigDecimal monto) {
        validarActiva();
        if (monto.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("El monto debe ser mayor a 0.");

        BigDecimal interes = monto.multiply(TASA_INTERES)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalDebitar = monto.add(interes);

        if (totalDebitar.compareTo(this.saldo) > 0)
            throw new SaldoInsuficienteException(
                    "Saldo insuficiente. Necesita: " + totalDebitar
                            + ", disponible: " + this.saldo);

        retirarInterno(totalDebitar, TipoMovimiento.RETIRO,
                "Retiro $" + monto + " + interés $" + interes);
        return interes;
    }
}