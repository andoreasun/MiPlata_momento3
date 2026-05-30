package com.miplata.infrastructure.persistence.mapper;

import com.miplata.domain.model.*;
import com.miplata.infrastructure.persistence.entity.CuentaEntity;
import com.miplata.infrastructure.persistence.entity.MovimientoEntity;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;

@Component
public class CuentaMapper {

    public Cuenta toDomain(CuentaEntity e) {
        UUID clienteId = e.getCliente() != null ? e.getCliente().getId() : null;
        Cuenta cuenta = switch (e.getTipoCuenta()) {
            case AHORROS -> new CuentaAhorros(
                    e.getId(), e.getNumeroCuenta(), e.getSaldo(), e.getEstado(), clienteId);
            case CORRIENTE -> new CuentaCorriente(
                    e.getId(), e.getNumeroCuenta(), e.getSaldo(), e.getEstado(), clienteId);
            case TARJETA_CREDITO -> new TarjetaCredito(
                    e.getId(), e.getNumeroCuenta(), e.getCupoTotal(),
                    e.getCupoDisponible(), e.getSaldo(), e.getEstado(), clienteId);
        };

        if (e.getMovimientos() != null) {
            e.getMovimientos().forEach(me ->
                    cuenta.getMovimientos().add(
                            Movimiento.builder()
                                    .id(me.getId())
                                    .tipo(me.getTipo())
                                    .valor(me.getValor())
                                    .fecha(me.getFecha())
                                    .descripcion(me.getDescripcion())
                                    .cuentaId(e.getId())
                                    .build()
                    ));
        }
        return cuenta;
    }

    public CuentaEntity toEntity(Cuenta d) {
        CuentaEntity e = CuentaEntity.builder()
                .id(d.getId())
                .numeroCuenta(d.getNumeroCuenta())
                .tipoCuenta(d.getTipoCuenta())
                .saldo(d.getSaldo())
                .estado(d.getEstado())
                .build();

        if (d instanceof TarjetaCredito t) {
            e.setCupoTotal(t.getCupoTotal());
            e.setCupoDisponible(t.getCupoDisponible());
        }

        List<MovimientoEntity> movs = d.getMovimientos().stream()
                .map(m -> MovimientoEntity.builder()
                        .id(m.getId())
                        .tipo(m.getTipo())
                        .valor(m.getValor())
                        .fecha(m.getFecha())
                        .descripcion(m.getDescripcion())
                        .cuenta(e)
                        .build())
                .toList();
        e.setMovimientos(movs);
        return e;
    }
}