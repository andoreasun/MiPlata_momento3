package com.miplata.adapter.out.persistence;

import com.miplata.application.port.out.CuentaRepositoryPort;
import com.miplata.domain.model.Cuenta;
import com.miplata.infrastructure.persistence.entity.CuentaEntity;
import com.miplata.infrastructure.persistence.mapper.CuentaMapper;
import com.miplata.infrastructure.persistence.repository.ClienteJpaRepository;
import com.miplata.infrastructure.persistence.repository.CuentaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CuentaRepositoryAdapter implements CuentaRepositoryPort {

    private final CuentaJpaRepository jpa;
    private final CuentaMapper mapper;
    private final ClienteJpaRepository clienteJpa;

    @Override
    public Cuenta guardar(Cuenta cuenta) {
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