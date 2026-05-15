package com.miplata.infrastructure.persistence.repository;

import com.miplata.infrastructure.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ClienteJpaRepository
        extends JpaRepository<ClienteEntity, UUID> {
    Optional<ClienteEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}