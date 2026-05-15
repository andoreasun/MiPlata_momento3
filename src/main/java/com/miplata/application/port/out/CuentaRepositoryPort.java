package com.miplata.application.port.out;

import com.miplata.domain.model.Cuenta;
import java.util.Optional;
import java.util.UUID;

public interface CuentaRepositoryPort {
    Cuenta guardar(Cuenta cuenta);
    Optional<Cuenta> buscarPorId(UUID id);
    Optional<Cuenta> buscarPorNumero(String numeroCuenta);
    void eliminar(UUID id);
}