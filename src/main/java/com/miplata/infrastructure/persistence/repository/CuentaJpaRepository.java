package com.miplata.infrastructure.persistence.repository;

import com.miplata.infrastructure.persistence.entity.CuentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CuentaJpaRepository
        extends JpaRepository<CuentaEntity, UUID> {
    Optional<CuentaEntity> findByNumeroCuenta(String numeroCuenta);
}