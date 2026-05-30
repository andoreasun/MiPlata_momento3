package com.miplata.adapter.out.persistence;

import com.miplata.application.port.out.CuentaRepositoryPort;
import com.miplata.domain.model.Cuenta;
import com.miplata.domain.model.TarjetaCredito;
import com.miplata.infrastructure.persistence.entity.CuentaEntity;
import com.miplata.infrastructure.persistence.entity.MovimientoEntity;
import com.miplata.infrastructure.persistence.mapper.CuentaMapper;
import com.miplata.infrastructure.persistence.repository.ClienteJpaRepository;
import com.miplata.infrastructure.persistence.repository.CuentaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CuentaRepositoryAdapter implements CuentaRepositoryPort {

    private final CuentaJpaRepository jpa;
    private final CuentaMapper mapper;
    private final ClienteJpaRepository clienteJpa;

    @Override
    public Cuenta guardar(Cuenta cuenta) {
        Optional<CuentaEntity> optExistente = jpa.findById(cuenta.getId());

        if (optExistente.isPresent()) {
            // Actualizar la entidad GESTIONADA directamente (evita problemas de merge con colecciones)
            CuentaEntity existing = optExistente.get();
            existing.setSaldo(cuenta.getSaldo());
            existing.setEstado(cuenta.getEstado());

            if (cuenta instanceof TarjetaCredito t) {
                existing.setCupoTotal(t.getCupoTotal());
                existing.setCupoDisponible(t.getCupoDisponible());
            }

            // Agregar solo los movimientos nuevos (los que aún no están en BD)
            Set<UUID> idsExistentes = existing.getMovimientos().stream()
                    .map(MovimientoEntity::getId)
                    .collect(Collectors.toSet());

            cuenta.getMovimientos().stream()
                    .filter(m -> !idsExistentes.contains(m.getId()))
                    .map(m -> MovimientoEntity.builder()
                            .id(m.getId())
                            .tipo(m.getTipo())
                            .valor(m.getValor())
                            .fecha(m.getFecha())
                            .descripcion(m.getDescripcion())
                            .cuenta(existing)
                            .build())
                    .forEach(existing.getMovimientos()::add);

            return mapper.toDomain(jpa.save(existing));
        }

        // Cuenta nueva: persistir por primera vez
        CuentaEntity entity = mapper.toEntity(cuenta);
        if (cuenta.getClienteId() != null) {
            clienteJpa.findById(cuenta.getClienteId())
                    .ifPresent(entity::setCliente);
        }
        return mapper.toDomain(jpa.save(entity));
    }

    @Override
    public Optional<Cuenta> buscarPorId(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Cuenta> buscarPorNumero(String numero) {
        return jpa.findByNumeroCuenta(numero).map(mapper::toDomain);
    }

    @Override
    public void eliminar(UUID id) {
        jpa.deleteById(id);
    }
}