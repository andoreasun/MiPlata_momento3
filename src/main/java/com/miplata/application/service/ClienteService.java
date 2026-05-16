package com.miplata.application.service;

import com.miplata.application.dto.*;
import com.miplata.application.port.in.ClienteUseCase;
import com.miplata.application.port.out.ClienteRepositoryPort;
import com.miplata.application.port.out.CuentaRepositoryPort;
import com.miplata.domain.enums.EstadoCliente;
import com.miplata.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService implements ClienteUseCase {

    private final ClienteRepositoryPort clienteRepo;
    private final CuentaRepositoryPort cuentaRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ClienteResponse registrar(RegistroClienteRequest req) {
        if (!req.getPassword().equals(req.getConfirmPassword()))
            throw new IllegalArgumentException(
                    "Las contraseñas no coinciden.");
        if (clienteRepo.existeUsername(req.getUsername()))
            throw new IllegalArgumentException(
                    "El nombre de usuario ya existe.");

        Cliente cliente = Cliente.builder()
                .id(UUID.randomUUID())
                .cedula(req.getCedula())
                .nombreCompleto(req.getNombreCompleto())
                .celular(req.getCelular())
                .username(req.getUsername())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .estado(EstadoCliente.ACTIVO)
                .intentosFallidos(0)
                .build();

        clienteRepo.guardar(cliente);
        return toResponse(cliente);
    }

    @Override
    public LoginResponse login(LoginRequest req) {
        Optional<com.miplata.domain.model.Cliente> opt =
                clienteRepo.buscarPorUsername(req.getUsername());
        if (opt.isEmpty())
            return LoginResponse.builder()
                    .autenticado(false)
                    .mensaje("Usuario no encontrado.")
                    .build();

        Cliente cliente = opt.get();
        try {
            boolean ok = passwordEncoder.matches(
                    req.getPassword(), cliente.getPasswordHash());
            cliente.autenticar(ok);
            clienteRepo.guardar(cliente);
            return LoginResponse.builder()
                    .clienteId(cliente.getId().toString())
                    .nombreCompleto(cliente.getNombreCompleto())
                    .autenticado(true)
                    .intentosRestantes(3)
                    .mensaje("Bienvenido, " + cliente.getNombreCompleto())
                    .build();
        } catch (Exception e) {
            clienteRepo.guardar(cliente);
            return LoginResponse.builder()
                    .autenticado(false)
                    .intentosRestantes(cliente.getIntentosRestantes())
                    .mensaje(e.getMessage())
                    .build();
        }
    }

    @Override
    public void cambiarPassword(CambioPasswordRequest req) {
        if (!req.getPasswordNuevo().equals(req.getConfirmPasswordNuevo()))
            throw new IllegalArgumentException(
                    "La confirmación no coincide.");
        Cliente cliente = clienteRepo.buscarPorUsername(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuario no encontrado."));
        boolean ok = passwordEncoder.matches(
                req.getPasswordActual(), cliente.getPasswordHash());
        cliente.cambiarPassword(ok,
                passwordEncoder.encode(req.getPasswordNuevo()));
        clienteRepo.guardar(cliente);
    }

    @Override
    public void desbloquearCliente(UUID clienteId) {
        Cliente cliente = getCliente(clienteId);
        cliente.desbloquear();
        clienteRepo.guardar(cliente);
    }

    @Override
    public List<ClienteResponse> listarTodos() {
        return clienteRepo.listarTodos().stream()
                .map(this::toResponse).toList();
    }

    @Override
    public void eliminarCliente(UUID clienteId) {
        clienteRepo.eliminar(clienteId);
    }

    @Override
    public CuentaResponse abrirCuenta(UUID clienteId,
                                      AbrirCuentaRequest req) {
        Cliente cliente = getCliente(clienteId);
        String numero = "MP" + System.currentTimeMillis();
        UUID cuentaId = UUID.randomUUID();

        Cuenta cuenta = switch (req.getTipoCuenta()) {
            case AHORROS -> new CuentaAhorros(cuentaId, numero,
                    req.getSaldoInicial() != null
                            ? req.getSaldoInicial() : BigDecimal.ZERO,
                    clienteId);
            case CORRIENTE -> new CuentaCorriente(cuentaId, numero,
                    req.getSaldoInicial() != null
                            ? req.getSaldoInicial() : BigDecimal.ZERO,
                    clienteId);
            case TARJETA_CREDITO -> new TarjetaCredito(cuentaId,
                    numero, req.getCupoCredito(), clienteId);
        };

        cuentaRepo.guardar(cuenta);
        cliente.agregarCuenta(cuenta);
        clienteRepo.guardar(cliente);

        return CuentaResponse.builder()
                .id(cuentaId.toString())
                .numeroCuenta(numero)
                .tipoCuenta(req.getTipoCuenta())
                .saldo(cuenta.getSaldo())
                .estado(cuenta.getEstado())
                .build();
    }

    @Override
    public ClienteResponse obtenerCliente(UUID clienteId) {
        Cliente c = getCliente(clienteId);
        return toResponseConCuentas(c);
    }

    private Cliente getCliente(UUID id) {
        return clienteRepo.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cliente no encontrado."));
    }

    private ClienteResponse toResponse(Cliente c) {
        return ClienteResponse.builder()
                .id(c.getId().toString())
                .cedula(c.getCedula())
                .nombreCompleto(c.getNombreCompleto())
                .celular(c.getCelular())
                .username(c.getUsername())
                .estado(c.getEstado())
                .intentosFallidos(c.getIntentosFallidos())
                .build();
    }

    private ClienteResponse toResponseConCuentas(Cliente c) {
        List<CuentaResponse> cuentas = c.getCuentas().stream()
                .map(cuenta -> {
                    CuentaResponse.CuentaResponseBuilder b = CuentaResponse.builder()
                            .id(cuenta.getId().toString())
                            .numeroCuenta(cuenta.getNumeroCuenta())
                            .tipoCuenta(cuenta.getTipoCuenta())
                            .saldo(cuenta.getSaldo())
                            .estado(cuenta.getEstado());
                    if (cuenta instanceof TarjetaCredito t)
                        b.cupoDisponible(t.getCupoDisponible());
                    return b.build();
                }).toList();
        return ClienteResponse.builder()
                .id(c.getId().toString())
                .cedula(c.getCedula())
                .nombreCompleto(c.getNombreCompleto())
                .celular(c.getCelular())
                .username(c.getUsername())
                .estado(c.getEstado())
                .intentosFallidos(c.getIntentosFallidos())
                .cuentas(cuentas)
                .build();
    }
}
