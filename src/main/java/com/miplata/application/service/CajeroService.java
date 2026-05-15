package com.miplata.application.service;

import com.miplata.application.dto.*;
import com.miplata.application.port.in.CajeroUseCase;
import com.miplata.application.port.out.CuentaRepositoryPort;
import com.miplata.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CajeroService implements CajeroUseCase {

    private final CuentaRepositoryPort cuentaRepo;

    @Override
    public SaldoResponse consultarSaldo(UUID cuentaId) {
        Cuenta cuenta = getCuenta(cuentaId);
        return SaldoResponse.builder()
                .numeroCuenta(cuenta.getNumeroCuenta())
                .saldo(cuenta.consultarSaldo())
                .tipoCuenta(cuenta.getTipoCuenta().name())
                .build();
    }

    @Override
    public RetiroResponse retirar(UUID cuentaId, BigDecimal monto) {
        Cuenta cuenta = getCuenta(cuentaId);
        BigDecimal interes = cuenta.retirar(monto);
        cuentaRepo.guardar(cuenta);
        return RetiroResponse.builder()
                .montoRetirado(monto)
                .interesAplicado(interes)
                .saldoResultante(cuenta.getSaldo())
                .build();
    }

    @Override
    public void consignar(UUID cuentaId, BigDecimal monto) {
        Cuenta cuenta = getCuenta(cuentaId);
        cuenta.consignar(monto);
        cuentaRepo.guardar(cuenta);
    }

    @Override
    public TransferenciaResponse transferir(UUID origenId,
                                            UUID destinoId,
                                            BigDecimal monto) {
        Cuenta origen = getCuenta(origenId);
        Cuenta destino = getCuenta(destinoId);
        origen.transferirA(destino, monto);
        cuentaRepo.guardar(origen);
        cuentaRepo.guardar(destino);
        return TransferenciaResponse.builder()
                .cuentaOrigen(origen.getNumeroCuenta())
                .cuentaDestino(destino.getNumeroCuenta())
                .monto(monto)
                .saldoOrigenResultante(origen.getSaldo())
                .build();
    }

    @Override
    public List<MovimientoDto> obtenerMovimientos(UUID cuentaId) {
        Cuenta cuenta = getCuenta(cuentaId);
        return cuenta.obtenerMovimientos().stream()
                .map(m -> MovimientoDto.builder()
                        .id(m.getId().toString())
                        .tipo(m.getTipo())
                        .valor(m.getValor())
                        .fecha(m.getFecha())
                        .descripcion(m.getDescripcion())
                        .build())
                .toList();
    }

    @Override
    public CompraResponse comprarConCredito(UUID tarjetaId,
                                            BigDecimal monto,
                                            int cuotas) {
        Cuenta cuenta = getCuenta(tarjetaId);
        if (!(cuenta instanceof TarjetaCredito tarjeta))
            throw new IllegalArgumentException(
                    "La cuenta no es una tarjeta de crédito.");
        BigDecimal cuota = tarjeta.comprar(monto, cuotas);
        cuentaRepo.guardar(tarjeta);
        return CompraResponse.builder()
                .montoCompra(monto)
                .cuotas(cuotas)
                .cuotaMensual(cuota)
                .cupoDisponibleResultante(tarjeta.getCupoDisponible())
                .resumen("Compra de $" + monto + " en " + cuotas
                        + " cuota(s) de $" + cuota + "/mes")
                .build();
    }

    private Cuenta getCuenta(UUID id) {
        return cuentaRepo.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cuenta no encontrada: " + id));
    }
}