package com.miplata.domain.model;

import com.miplata.domain.enums.TipoMovimiento;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class Movimiento {
    private final UUID id;
    private final TipoMovimiento tipo;
    private final BigDecimal valor;
    private final LocalDateTime fecha;
    private final String descripcion;
    private final UUID cuentaId;

    public static Movimiento crear(TipoMovimiento tipo, BigDecimal valor,
                                   UUID cuentaId, String descripcion) {
        return Movimiento.builder()
                .id(UUID.randomUUID())
                .tipo(tipo)
                .valor(valor)
                .fecha(LocalDateTime.now())
                .descripcion(descripcion)
                .cuentaId(cuentaId)
                .build();
    }
}