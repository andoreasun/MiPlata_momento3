package com.miplata.application.port.in;

import com.miplata.application.dto.*;
import java.util.List;
import java.util.UUID;

public interface ClienteUseCase {
    ClienteResponse registrar(RegistroClienteRequest request);
    LoginResponse login(LoginRequest request);
    void cambiarPassword(CambioPasswordRequest request);
    void desbloquearCliente(UUID clienteId);
    List<ClienteResponse> listarTodos();
    void eliminarCliente(UUID clienteId);
    CuentaResponse abrirCuenta(UUID clienteId,
                               AbrirCuentaRequest request);
}