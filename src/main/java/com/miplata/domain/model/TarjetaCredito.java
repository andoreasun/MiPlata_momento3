package com.miplata.domain.model;

import com.miplata.domain.enums.RangoCuotas;
import com.miplata.domain.enums.TipoCuenta;
import com.miplata.domain.enums.TipoMovimiento;
import com.miplata.domain.exception.CupoExcedidoException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * EPI 1 – Motor lógico POO | MP-4: TarjetaCredito – cuotas y motor financiero
 *
 * Como: cliente con tarjeta de crédito.
 * Quiero: financiar compras en cuotas con la tasa correspondiente.
 * Para: que el sistema calcule automáticamente mi cuota mensual.
 *
 * Criterios de aceptación:
 * - Hasta 2 cuotas: tasa 0%
 * - 3 a 6 cuotas: tasa 1.9% mensual
 * - 7 o más cuotas: tasa 2.3% mensual
 * - Fórmula: Cuota = (Capital × tasa) / (1 − (1+tasa)^−n)
 * - Toda transacción muestra el valor de cuota mensual resultante
 * - El cupo disponible se reduce con cada compra y no puede superarse
 */
public class TarjetaCredito extends Cuenta {

    private final BigDecimal cupoTotal;
    private BigDecimal cupoDisponible;

    public TarjetaCredito(UUID id, String numeroCuenta,
                          BigDecimal cupoTotal, UUID clienteId) {
        super(id, numeroCuenta, TipoCuenta.TARJETA_CREDITO,
                BigDecimal.ZERO, clienteId);
        this.cupoTotal = cupoTotal;
        this.cupoDisponible = cupoTotal;
    }

    @Override
    public BigDecimal retirar(BigDecimal monto) {
        throw new UnsupportedOperationException(
                "Use comprar(monto, cuotas) para tarjeta de crédito.");
    }

    public BigDecimal comprar(BigDecimal monto, int cuotas) {
        validarActiva();
        if (monto.compareTo(cupoDisponible) > 0)
            throw new CupoExcedidoException(
                    "Cupo insuficiente. Disponible: " + cupoDisponible);

        BigDecimal cuotaMensual = calcularCuota(monto, cuotas);
        this.cupoDisponible = this.cupoDisponible.subtract(monto);
        this.saldo = this.saldo.add(monto);

        registrar(TipoMovimiento.COMPRA_CREDITO, monto,
                "Compra $" + monto + " en " + cuotas
                        + " cuotas de $" + cuotaMensual);
        return cuotaMensual;
    }

    public BigDecimal calcularCuota(BigDecimal capital, int cuotas) {
        BigDecimal tasa = RangoCuotas.tasaPara(cuotas);
        if (tasa.compareTo(BigDecimal.ZERO) == 0) {
            return capital.divide(BigDecimal.valueOf(cuotas),
                    2, RoundingMode.HALF_UP);
        }
        MathContext mc = new MathContext(10);
        BigDecimal unoPlusTasa = BigDecimal.ONE.add(tasa);
        BigDecimal denominador = BigDecimal.ONE.subtract(
                unoPlusTasa.pow(-cuotas, mc));
        return capital.multiply(tasa)
                .divide(denominador, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getCupoDisponible() { return cupoDisponible; }
    public BigDecimal getCupoTotal() { return cupoTotal; }
}