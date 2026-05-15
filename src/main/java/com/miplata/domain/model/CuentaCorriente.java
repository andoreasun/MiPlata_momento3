package com.miplata.domain.model;

import com.miplata.domain.enums.TipoCuenta;
import com.miplata.domain.enums.TipoMovimiento;
import com.miplata.domain.exception.SaldoInsuficienteException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * EPI 1 – Motor lógico POO | MP-3: CuentaCorriente – sobregiro del 20%
 *
 * Como: cliente con cuenta corriente.
 * Quiero: poder retirar hasta un 20% adicional sobre mi saldo actual.
 * Para: cubrir pagos de nómina y gestión diaria cuando el saldo es insuficiente.
 *
 * Criterios de aceptación:
 * - Override de retirar() permite monto <= (saldo + 20% del saldo)
 * - No genera intereses en operaciones normales
 * - La operación de transferencia frecuente funciona correctamente
 * - Registra movimientos con tipo RETIRO o TRANSFERENCIA_DEBITO
 */
public class CuentaCorriente extends Cuenta {

    private static final BigDecimal FACTOR_SOBREGIRO = new BigDecimal("1.20");

    public CuentaCorriente(UUID id, String numeroCuenta,
                           BigDecimal saldoInicial, UUID clienteId) {
        super(id, numeroCuenta, TipoCuenta.CORRIENTE, saldoInicial, clienteId);
    }

    @Override
    public BigDecimal retirar(BigDecimal monto) {
        validarActiva();
        BigDecimal limiteMaximo = this.saldo.multiply(FACTOR_SOBREGIRO)
                .setScale(2, RoundingMode.HALF_UP);

        if (monto.compareTo(limiteMaximo) > 0)
            throw new SaldoInsuficienteException(
                    "Supera el límite de sobregiro. Máximo permitido: "
                            + limiteMaximo);

        retirarInterno(monto, TipoMovimiento.RETIRO,
                "Retiro en cuenta corriente");
        return BigDecimal.ZERO;
    }
}