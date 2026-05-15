package com.miplata.adapter.in.rest;

import com.miplata.application.dto.*;
import com.miplata.application.port.in.CajeroUseCase;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * EPI 3 – Módulo transaccional | MP-9: Consignar | MP-10: Transferencias | MP-11: Consulta
 */
@RestController
@RequestMapping("/api/cajero")
@RequiredArgsConstructor
public class CajeroController {

    private final CajeroUseCase cajeroUseCase;

    /** MP-11: Consulta de saldo – GET /api/cajero/cuentas/{id}/saldo */
    @GetMapping("/cuentas/{id}/saldo")
    public ResponseEntity<SaldoResponse> consultarSaldo(
            @PathVariable UUID id) {
        return ResponseEntity.ok(cajeroUseCase.consultarSaldo(id));
    }

    /** MP-11: Extracto de movimientos descendente por fecha – GET /api/cajero/cuentas/{id}/movimientos */
    @GetMapping("/cuentas/{id}/movimientos")
    public ResponseEntity<List<MovimientoDto>> movimientos(
            @PathVariable UUID id) {
        return ResponseEntity.ok(
                cajeroUseCase.obtenerMovimientos(id));
    }

    /**
     * MP-9: Consignar fondos con actualización en tiempo real
     * Como: cliente. Quiero: consignar dinero en cualquiera de mis productos.
     * Para: ver mi saldo actualizado inmediatamente.
     * Criterios: monto > 0, registra movimiento tipo CONSIGNACION, alerta éxito/error.
     */
    @PostMapping("/cuentas/{id}/consignar")
    public ResponseEntity<Void> consignar(
            @PathVariable UUID id,
            @RequestParam @Positive BigDecimal monto) {
        cajeroUseCase.consignar(id, monto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cuentas/{id}/retirar")
    public ResponseEntity<RetiroResponse> retirar(
            @PathVariable UUID id,
            @RequestParam @Positive BigDecimal monto) {
        return ResponseEntity.ok(cajeroUseCase.retirar(id, monto));
    }

    /**
     * MP-10: Transferencias entre productos y a terceros
     * Como: cliente. Quiero: transferir dinero entre mis cuentas o a cuentas de terceros.
     * Para: mover fondos sin restricción de tipo de producto destino, excepto al mismo producto.
     * Criterios: prohibido al mismo producto, saldo se mueve atómicamente, movimiento en ambas cuentas.
     */
    @PostMapping("/cuentas/{origenId}/transferir/{destinoId}")
    public ResponseEntity<TransferenciaResponse> transferir(
            @PathVariable UUID origenId,
            @PathVariable UUID destinoId,
            @RequestParam @Positive BigDecimal monto) {
        return ResponseEntity.ok(
                cajeroUseCase.transferir(origenId, destinoId, monto));
    }

    @PostMapping("/tarjetas/{id}/comprar")
    public ResponseEntity<CompraResponse> comprar(
            @PathVariable UUID id,
            @RequestParam @Positive BigDecimal monto,
            @RequestParam int cuotas) {
        return ResponseEntity.ok(
                cajeroUseCase.comprarConCredito(id, monto, cuotas));
    }
}