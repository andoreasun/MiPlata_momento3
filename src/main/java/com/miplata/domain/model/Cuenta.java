package com.miplata.domain.model;

import com.miplata.domain.enums.EstadoCuenta;
import com.miplata.domain.enums.TipoCuenta;
import com.miplata.domain.enums.TipoMovimiento;
import com.miplata.domain.exception.CuentaBloqueadaException;
import com.miplata.domain.exception.MontoInvalidoException;
import com.miplata.domain.exception.TransferenciaInvalidaException;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.*;

/**
 * EPI 1 – Motor lógico POO | MP-1: Clase abstracta Cuenta e interfaces del sistema
 *
 * Como: cliente del sistema bancario.
 * Quiero: una estructura base común para todos los productos financieros.
 * Para: garantizar un comportamiento coherente entre cuentas de ahorros, corriente y tarjetas.
 *
 * Criterios de aceptación:
 * - Clase abstracta con atributos comunes: id, numeroCuenta, saldo, estado, movimientos
 * - Método abstracto retirar() implementado con reglas propias en cada subclase
 * - Métodos concretos: consignar(), transferirA(), consultarSaldo(), obtenerMovimientos()
 * - Movimientos ordenados de forma descendente por fecha
 * - No permite operaciones sobre cuentas inactivas o bloqueadas
 */
@Getter
public abstract class Cuenta {

    protected final UUID id;
    protected final String numeroCuenta;
    protected final TipoCuenta tipoCuenta;
    protected BigDecimal saldo;
    protected EstadoCuenta estado;
    protected final UUID clienteId;
    protected final List<Movimiento> movimientos;

    protected Cuenta(UUID id, String numeroCuenta, TipoCuenta tipoCuenta,
                     BigDecimal saldoInicial, UUID clienteId) {
        this.id = id;
        this.numeroCuenta = numeroCuenta;
        this.tipoCuenta = tipoCuenta;
        this.saldo = saldoInicial;
        this.estado = EstadoCuenta.ACTIVA;
        this.clienteId = clienteId;
        this.movimientos = new ArrayList<>();
    }

    public void consignar(BigDecimal monto) {
        validarActiva();
        if (monto.compareTo(BigDecimal.ZERO) <= 0)
            throw new MontoInvalidoException("El monto debe ser mayor a 0.");
        this.saldo = this.saldo.add(monto);
        registrar(TipoMovimiento.CONSIGNACION, monto, "Consignación");
    }

    public abstract BigDecimal retirar(BigDecimal monto);

    public BigDecimal consultarSaldo() {
        return this.saldo;
    }

    public List<Movimiento> obtenerMovimientos() {
        return movimientos.stream()
                .sorted(Comparator.comparing(Movimiento::getFecha).reversed())
                .toList();
    }

    public void validarDestino(Cuenta destino) {
        if (this.id.equals(destino.getId()))
            throw new TransferenciaInvalidaException(
                    "Prohibido transferir al mismo producto.");
    }

    public void transferirA(Cuenta destino, BigDecimal monto) {
        validarDestino(destino);
        validarActiva();
        retirarInterno(monto, TipoMovimiento.TRANSFERENCIA_DEBITO,
                "Transferencia a " + destino.getNumeroCuenta());
        destino.recibirTransferencia(monto, this.numeroCuenta);
    }

    public void recibirTransferencia(BigDecimal monto, String origen) {
        this.saldo = this.saldo.add(monto);
        registrar(TipoMovimiento.TRANSFERENCIA_CREDITO, monto,
                "Transferencia desde " + origen);
    }

    protected void validarActiva() {
        if (estado != EstadoCuenta.ACTIVA)
            throw new CuentaBloqueadaException(
                    "La cuenta " + numeroCuenta + " no está activa.");
    }

    protected void retirarInterno(BigDecimal monto, TipoMovimiento tipo,
                                  String desc) {
        this.saldo = this.saldo.subtract(monto);
        registrar(tipo, monto, desc);
    }

    protected void registrar(TipoMovimiento tipo, BigDecimal valor,
                             String desc) {
        movimientos.add(Movimiento.crear(tipo, valor, this.id, desc));
    }
}