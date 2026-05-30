package com.miplata;

import com.miplata.application.dto.*;
import com.miplata.application.port.in.CajeroUseCase;
import com.miplata.application.port.in.ClienteUseCase;
import com.miplata.domain.enums.TipoCuenta;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * EPI 4 – UI/UX | MP-12: Navegación fluida
 * Menú interactivo en consola que cubre las 12 historias de usuario (MP-1 al MP-12).
 */
@Component
@RequiredArgsConstructor
public class ConsoleMenuRunner implements CommandLineRunner {

    private final ClienteUseCase clienteUseCase;
    private final CajeroUseCase cajeroUseCase;
    private final DataSource dataSource;
    private final Scanner scanner = new Scanner(System.in);

    private String clienteIdSesion = null;
    private String usernameSesion  = null;
    private String nombreSesion    = null;
    private List<CuentaResponse> cuentasSesion = new ArrayList<>();

    @Override
    public void run(String... args) {
        println("============================================");
        println("        MI PLATA - Sistema Bancario        ");
        println("============================================");
        verificarConexionDB();

        println("\nPresione 1 para iniciar la aplicación...");
        while (!leer("").equals("1")) {
            println("Presione 1 para continuar.");
        }

        boolean continuar = true;
        while (continuar) {
            println("\n=== MENÚ PRINCIPAL ===");
            println("1. Registrar nuevo cliente");
            println("2. Iniciar sesión");
            println("3. Panel Administrador");
            println("0. Salir");
            String opcion = leer("Seleccione una opción: ");

            switch (opcion) {
                case "1" -> registrarCliente();
                case "2" -> iniciarSesion();
                case "3" -> menuAdmin();
                case "0" -> { println("¡Hasta luego!"); continuar = false; }
                default  -> println("[!] Opción no válida.");
            }
        }
    }

    // ── MP-5: Registro ──────────────────────────────────────────────────────

    private void registrarCliente() {
        println("\n--- Registro de nuevo cliente ---");
        RegistroClienteRequest req = new RegistroClienteRequest();
        req.setCedula(leer("Cédula/Pasaporte: "));
        req.setNombreCompleto(leer("Nombre completo: "));
        req.setCelular(leer("Celular: "));
        req.setUsername(leer("Usuario (mín. 4 caracteres): "));
        req.setPassword(leer("Contraseña (mín. 6 caracteres): "));
        req.setConfirmPassword(leer("Confirmar contraseña: "));
        try {
            ClienteResponse resp = clienteUseCase.registrar(req);
            println("[OK] Cliente registrado: " + resp.getNombreCompleto()
                    + " | ID: " + resp.getId());
        } catch (Exception e) {
            println("[ERROR] " + e.getMessage());
        }
        pausar();
    }

    // ── MP-6: Login ──────────────────────────────────────────────────────────

    private void iniciarSesion() {
        println("\n--- Iniciar sesión ---");
        LoginRequest req = new LoginRequest();
        req.setUsername(leer("Usuario: "));
        req.setPassword(leer("Contraseña: "));
        try {
            LoginResponse resp = clienteUseCase.login(req);
            if (resp.isAutenticado()) {
                clienteIdSesion = resp.getClienteId();
                usernameSesion  = req.getUsername();
                nombreSesion    = resp.getNombreCompleto();
                cargarCuentas();
                println("[OK] " + resp.getMensaje());
                menuCliente();
            } else {
                println("[!] " + resp.getMensaje());
                if (resp.getIntentosRestantes() > 0)
                    println("    Intentos restantes: " + resp.getIntentosRestantes());
                pausar();
            }
        } catch (Exception e) {
            println("[ERROR] " + e.getMessage());
            pausar();
        }
    }

    private void cargarCuentas() {
        try {
            ClienteResponse cliente = clienteUseCase.obtenerCliente(
                    UUID.fromString(clienteIdSesion));
            cuentasSesion = cliente.getCuentas() != null
                    ? new ArrayList<>(cliente.getCuentas())
                    : new ArrayList<>();
        } catch (Exception e) {
            cuentasSesion = new ArrayList<>();
        }
    }

    // ── Selección de cuenta por número ───────────────────────────────────────

    private CuentaResponse seleccionarCuenta() {
        if (cuentasSesion.isEmpty()) {
            println("[!] No tienes cuentas registradas. Abre una primero.");
            return null;
        }
        println("\n  Tus cuentas:");
        for (int i = 0; i < cuentasSesion.size(); i++) {
            CuentaResponse c = cuentasSesion.get(i);
            println(String.format("  %d. [%-15s] %-20s | Saldo: $%.2f",
                    i + 1, c.getTipoCuenta(), c.getNumeroCuenta(), c.getSaldo()));
        }
        String input = leer("  Seleccione cuenta (número): ");
        try {
            int idx = Integer.parseInt(input) - 1;
            if (idx < 0 || idx >= cuentasSesion.size()) {
                println("[!] Número inválido.");
                return null;
            }
            return cuentasSesion.get(idx);
        } catch (NumberFormatException e) {
            println("[!] Ingrese un número válido.");
            return null;
        }
    }

    // ── Menú de cliente autenticado ──────────────────────────────────────────

    private void menuCliente() {
        boolean activo = true;
        while (activo) {
            println("\n=== BIENVENIDO, " + nombreSesion.toUpperCase() + " ===");
            println("1.  Ver mis cuentas");
            println("2.  Abrir nueva cuenta");
            println("3.  Consultar saldo");
            println("4.  Ver extracto de movimientos");
            println("5.  Consignar");
            println("6.  Retirar");
            println("7.  Transferir");
            println("8.  Comprar con tarjeta de crédito");
            println("9.  Cambiar contraseña");
            println("0.  Cerrar sesión");
            String opcion = leer("Seleccione una opción: ");

            switch (opcion) {
                case "1" -> verCuentas();
                case "2" -> abrirCuenta();
                case "3" -> consultarSaldo();
                case "4" -> verExtracto();
                case "5" -> consignar();
                case "6" -> retirar();
                case "7" -> transferir();
                case "8" -> comprarConCredito();
                case "9" -> cambiarPassword();
                case "0" -> {
                    println("Sesión cerrada.");
                    clienteIdSesion = null;
                    usernameSesion  = null;
                    nombreSesion    = null;
                    cuentasSesion.clear();
                    activo = false;
                }
                default -> println("[!] Opción no válida.");
            }
        }
    }

    // ── MP-1/2/3/4: Cuentas ─────────────────────────────────────────────────

    private void verCuentas() {
        println("\n--- Mis cuentas ---");
        cargarCuentas();
        if (cuentasSesion.isEmpty()) {
            println("No tienes cuentas registradas.");
            return;
        }
        for (int i = 0; i < cuentasSesion.size(); i++) {
            CuentaResponse c = cuentasSesion.get(i);
            println(String.format("  %d. [%-15s] %-20s | Saldo: $%.2f | Estado: %s",
                    i + 1, c.getTipoCuenta(), c.getNumeroCuenta(),
                    c.getSaldo(), c.getEstado()));
        }
        pausar();
    }

    private void abrirCuenta() {
        println("\n--- Abrir nueva cuenta ---");
        println("Tipo de cuenta:");
        println("1. AHORROS");
        println("2. CORRIENTE");
        println("3. TARJETA DE CRÉDITO");
        String tipo = leer("Seleccione: ");
        AbrirCuentaRequest req = new AbrirCuentaRequest();
        try {
            switch (tipo) {
                case "1" -> {
                    req.setTipoCuenta(TipoCuenta.AHORROS);
                    req.setSaldoInicial(new BigDecimal(leer("Saldo inicial: $")));
                }
                case "2" -> {
                    req.setTipoCuenta(TipoCuenta.CORRIENTE);
                    req.setSaldoInicial(new BigDecimal(leer("Saldo inicial: $")));
                }
                case "3" -> {
                    req.setTipoCuenta(TipoCuenta.TARJETA_CREDITO);
                    req.setCupoCredito(new BigDecimal(leer("Cupo de crédito: $")));
                }
                default -> { println("[!] Tipo no válido."); return; }
            }
            CuentaResponse resp = clienteUseCase.abrirCuenta(
                    UUID.fromString(clienteIdSesion), req);
            println("[OK] Cuenta abierta: " + resp.getNumeroCuenta()
                    + " | Saldo: $" + resp.getSaldo());
            cargarCuentas();
        } catch (Exception e) {
            println("[ERROR] " + e.getMessage());
        }
        pausar();
    }

    // ── MP-11: Saldo y extracto ──────────────────────────────────────────────

    private void consultarSaldo() {
        println("\n--- Consultar saldo ---");
        CuentaResponse cuenta = seleccionarCuenta();
        if (cuenta == null) return;
        try {
            SaldoResponse resp = cajeroUseCase.consultarSaldo(
                    UUID.fromString(cuenta.getId()));
            println("[OK] " + resp.getNumeroCuenta()
                    + " | " + resp.getTipoCuenta()
                    + " | Saldo: $" + resp.getSaldo());
        } catch (Exception e) {
            println("[ERROR] " + e.getMessage());
        }
        pausar();
    }

    private void verExtracto() {
        println("\n--- Extracto de movimientos ---");
        CuentaResponse cuenta = seleccionarCuenta();
        if (cuenta == null) return;
        try {
            List<MovimientoDto> movimientos = cajeroUseCase.obtenerMovimientos(
                    UUID.fromString(cuenta.getId()));
            if (movimientos.isEmpty()) {
                println("Sin movimientos registrados.");
                return;
            }
            println(String.format("%-26s %-20s %12s  %-30s",
                    "Fecha", "Tipo", "Valor", "Descripción"));
            println("-".repeat(90));
            for (MovimientoDto m : movimientos) {
                println(String.format("%-26s %-20s %12.2f  %-30s",
                        m.getFecha(), m.getTipo(), m.getValor(),
                        m.getDescripcion() != null ? m.getDescripcion() : ""));
            }
        } catch (Exception e) {
            println("[ERROR] " + e.getMessage());
        }
        pausar();
    }

    // ── MP-9: Consignar ──────────────────────────────────────────────────────

    private void consignar() {
        println("\n--- Consignar fondos ---");
        CuentaResponse cuenta = seleccionarCuenta();
        if (cuenta == null) return;
        String monto = leer("Monto a consignar: $");
        try {
            cajeroUseCase.consignar(UUID.fromString(cuenta.getId()),
                    new BigDecimal(monto));
            println("[OK] Consignación exitosa.");
            cargarCuentas();
        } catch (Exception e) {
            println("[ERROR] " + e.getMessage());
        }
        pausar();
    }

    // ── Retirar ──────────────────────────────────────────────────────────────

    private void retirar() {
        println("\n--- Retirar fondos ---");
        CuentaResponse cuenta = seleccionarCuenta();
        if (cuenta == null) return;
        String monto = leer("Monto a retirar: $");
        try {
            RetiroResponse resp = cajeroUseCase.retirar(
                    UUID.fromString(cuenta.getId()), new BigDecimal(monto));
            println("[OK] Monto retirado: $" + resp.getMontoRetirado()
                    + " | Interés: $" + resp.getInteresAplicado()
                    + " | Saldo resultante: $" + resp.getSaldoResultante());
            cargarCuentas();
        } catch (Exception e) {
            println("[ERROR] " + e.getMessage());
        }
        pausar();
    }

    // ── MP-10: Transferir ────────────────────────────────────────────────────

    private void transferir() {
        println("\n--- Transferencia ---");
        println("  Cuenta ORIGEN:");
        CuentaResponse origen = seleccionarCuenta();
        if (origen == null) return;

        println("  Cuenta DESTINO (puede ser de otro cliente):");
        println("  1. Una de mis cuentas");
        println("  2. Cuenta de tercero (por número de cuenta)");
        String modo = leer("  Seleccione: ");

        String destinoId;
        try {
            if (modo.equals("1")) {
                CuentaResponse destino = seleccionarCuenta();
                if (destino == null) return;
                destinoId = destino.getId();
            } else {
                String numeroCuenta = leer("  Número de cuenta destino: ");
                CuentaResponse cuentaDestino = cajeroUseCase.buscarCuentaPorNumero(numeroCuenta);
                if (cuentaDestino == null) {
                    println("[!] Cuenta destino no encontrada.");
                    return;
                }
                destinoId = cuentaDestino.getId();
            }

            String monto = leer("  Monto: $");
            TransferenciaResponse resp = cajeroUseCase.transferir(
                    UUID.fromString(origen.getId()),
                    UUID.fromString(destinoId),
                    new BigDecimal(monto));
            println("[OK] $" + resp.getMonto()
                    + " de " + resp.getCuentaOrigen()
                    + " → " + resp.getCuentaDestino()
                    + " | Saldo origen: $" + resp.getSaldoOrigenResultante());
            cargarCuentas();
        } catch (Exception e) {
            println("[ERROR] " + e.getMessage());
        }
        pausar();
    }

    // ── MP-4: Compra con tarjeta de crédito ──────────────────────────────────

    private void comprarConCredito() {
        println("\n--- Compra con tarjeta de crédito ---");
        CuentaResponse cuenta = seleccionarCuenta();
        if (cuenta == null) return;
        String monto  = leer("Monto de la compra: $");
        String cuotas = leer("Número de cuotas: ");
        try {
            CompraResponse resp = cajeroUseCase.comprarConCredito(
                    UUID.fromString(cuenta.getId()),
                    new BigDecimal(monto),
                    Integer.parseInt(cuotas));
            println("[OK] " + resp.getResumen());
            println("     Cupo disponible: $" + resp.getCupoDisponibleResultante());
            cargarCuentas();
        } catch (Exception e) {
            println("[ERROR] " + e.getMessage());
        }
        pausar();
    }

    // ── MP-7: Cambio de contraseña ───────────────────────────────────────────

    private void cambiarPassword() {
        println("\n--- Cambio de contraseña ---");
        CambioPasswordRequest req = new CambioPasswordRequest();
        req.setUsername(usernameSesion);
        req.setPasswordActual(leer("Contraseña actual: "));
        req.setPasswordNuevo(leer("Nueva contraseña: "));
        req.setConfirmPasswordNuevo(leer("Confirmar nueva contraseña: "));
        try {
            clienteUseCase.cambiarPassword(req);
            println("[OK] Contraseña actualizada correctamente.");
        } catch (Exception e) {
            println("[ERROR] " + e.getMessage());
        }
        pausar();
    }

    // ── MP-8: Panel Administrador ────────────────────────────────────────────

    private void menuAdmin() {
        println("\n--- Panel Administrador ---");
        boolean activo = true;
        while (activo) {
            println("\n=== PANEL ADMINISTRADOR ===");
            println("1. Listar todos los clientes");
            println("2. Desbloquear cliente");
            println("3. Eliminar cliente");
            println("0. Volver");
            String opcion = leer("Seleccione una opción: ");
            switch (opcion) {
                case "1" -> listarClientes();
                case "2" -> desbloquearCliente();
                case "3" -> eliminarCliente();
                case "0" -> activo = false;
                default  -> println("[!] Opción no válida.");
            }
        }
    }

    private void listarClientes() {
        List<ClienteResponse> clientes = clienteUseCase.listarTodos();
        if (clientes.isEmpty()) { println("Sin clientes registrados."); return; }
        println(String.format("%-36s  %-20s  %-15s  %-10s  %s",
                "ID", "Nombre", "Usuario", "Estado", "Intentos"));
        println("-".repeat(95));
        for (ClienteResponse c : clientes) {
            println(String.format("%-36s  %-20s  %-15s  %-10s  %d",
                    c.getId(), c.getNombreCompleto(), c.getUsername(),
                    c.getEstado(), c.getIntentosFallidos()));
        }
        pausar();
    }

    private void desbloquearCliente() {
        String id = leer("ID del cliente a desbloquear: ");
        try {
            clienteUseCase.desbloquearCliente(UUID.fromString(id));
            println("[OK] Cliente desbloqueado.");
        } catch (Exception e) {
            println("[ERROR] " + e.getMessage());
        }
        pausar();
    }

    private void eliminarCliente() {
        String id = leer("ID del cliente a eliminar: ");
        try {
            clienteUseCase.eliminarCliente(UUID.fromString(id));
            println("[OK] Cliente eliminado.");
        } catch (Exception e) {
            println("[ERROR] " + e.getMessage());
        }
        pausar();
    }

    // ── Conexión DB ──────────────────────────────────────────────────────────

    private void verificarConexionDB() {
        try (Connection conn = dataSource.getConnection()) {
            String db = conn.getCatalog();
            println("--------------------------------------------");
            println("  Base de datos: MySQL - " + db);
            println("  Estado: CONECTADO ✓");
            println("--------------------------------------------");
        } catch (Exception e) {
            println("--------------------------------------------");
            println("  Base de datos: ERROR DE CONEXION");
            println("  " + e.getMessage());
            println("--------------------------------------------");
        }
    }

    // ── Utilidades ───────────────────────────────────────────────────────────

    private String leer(String mensaje) {
        System.out.print(mensaje);
        System.out.flush();
        return scanner.nextLine().trim();
    }

    private void pausar() {
        println("\n--------------------------------------------");
        while (true) {
            String input = leer("Presione 1 para volver al menú: ");
            if (input.equals("1")) break;
            println("[!] Presione 1 para continuar.");
        }
        println("--------------------------------------------");
    }

    private void println(String texto) {
        System.out.println(texto);
    }
}
