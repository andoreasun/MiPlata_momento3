package com.miplata.adapter.out.persistence;

import com.miplata.application.port.out.ClienteRepositoryPort;
import com.miplata.domain.model.Cliente;
import com.miplata.infrastructure.persistence.mapper.ClienteMapper;
import com.miplata.infrastructure.persistence.repository.ClienteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository jpa;
    private final ClienteMapper mapper;

    @Override
    public Cliente guardar(Cliente cliente) {
        return mapper.toDomain(jpa.save(mapper.toEntity(cliente)));
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorUsername(String username) {
        return jpa.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    public boolean existeUsername(String username) {
        return jpa.existsByUsername(username);
    }

    @Override
    public List<Cliente> listarTodos() {
        return jpa.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void eliminar(UUID id) {
        jpa.deleteById(id);
    }
}