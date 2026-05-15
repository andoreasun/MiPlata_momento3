package com.miplata.adapter.in.rest;

import com.miplata.application.dto.*;
import com.miplata.application.port.in.ClienteUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * EPI 2 – Autenticación y gestión de clientes
 *
 * MP-5: Registro | MP-6: Login con bloqueo | MP-7: Cambio de contraseña | MP-8: Panel admin CRUD
 *
 * MP-8: Panel administrativo CRUD de usuarios
 * Como: administrador del sistema. Quiero: gestionar usuarios desde un panel oculto.
 * Para: poder agregar, editar o eliminar clientes de la plataforma.
 * Criterios:
 * - Panel no visible en navegación normal del cliente
 * - Permite crear, leer, actualizar y eliminar registros de usuario
 * - Al eliminar un usuario se eliminan también sus cuentas y movimientos
 * - Acceso especial sin flujo de autenticación normal
 */
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteUseCase clienteUseCase;

    /** MP-5: Registro de cliente con validación estricta – POST /api/clientes/registro */
    @PostMapping("/registro")
    public ResponseEntity<ClienteResponse> registrar(
            @Valid @RequestBody RegistroClienteRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteUseCase.registrar(req));
    }

    /** MP-6: Login con bloqueo por intentos fallidos – POST /api/clientes/login */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(clienteUseCase.login(req));
    }

    /** MP-7: Cambio de contraseña con fricción intencional – PUT /api/clientes/cambiar-password */
    @PutMapping("/cambiar-password")
    public ResponseEntity<Void> cambiarPassword(
            @Valid @RequestBody CambioPasswordRequest req) {
        clienteUseCase.cambiarPassword(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{clienteId}/cuentas")
    public ResponseEntity<CuentaResponse> abrirCuenta(
            @PathVariable UUID clienteId,
            @Valid @RequestBody AbrirCuentaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteUseCase.abrirCuenta(clienteId, req));
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listar() {
        return ResponseEntity.ok(clienteUseCase.listarTodos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        clienteUseCase.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/desbloquear")
    public ResponseEntity<Void> desbloquear(@PathVariable UUID id) {
        clienteUseCase.desbloquearCliente(id);
        return ResponseEntity.ok().build();
    }
}