package com.miplata.application.port.in;

import com.miplata.application.dto.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CajeroUseCase {
    SaldoResponse consultarSaldo(UUID cuentaId);
    RetiroResponse retirar(UUID cuentaId, BigDecimal monto);
    void consignar(UUID cuentaId, BigDecimal monto);
    TransferenciaResponse transferir(UUID cuentaOrigen,
                                     UUID cuentaDestino,
                                     BigDecimal monto);
    List<MovimientoDto> obtenerMovimientos(UUID cuentaId);
    CompraResponse comprarConCredito(UUID tarjetaId,
                                     BigDecimal monto,
                                     int cuotas);
    CuentaResponse buscarCuentaPorNumero(String numeroCuenta);
}