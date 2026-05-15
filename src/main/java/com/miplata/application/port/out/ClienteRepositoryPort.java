package com.miplata.application.port.out;

import com.miplata.domain.model.Cliente;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepositoryPort {
    Cliente guardar(Cliente cliente);
    Optional<Cliente> buscarPorId(UUID id);
    Optional<Cliente> buscarPorUsername(String username);
    boolean existeUsername(String username);
    List<Cliente> listarTodos();
    void eliminar(UUID id);
}