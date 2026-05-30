package com.miplata.domain.model;

import com.miplata.domain.enums.EstadoCliente;
import com.miplata.domain.exception.CredencialesInvalidasException;
import com.miplata.domain.exception.CuentaBloqueadaException;
import com.miplata.domain.interfaces.IAutenticable;
import lombok.Builder;
import lombok.Getter;
import java.util.*;

/**
 * EPI 2 – Autenticación y gestión de clientes
 *
 * MP-5: Registro de cliente con validación estricta
 * Como: nuevo usuario. Quiero: registrarme en Mi Plata. Para: poder acceder con credenciales seguras.
 * Criterios: Cédula, Nombre, Celular, Usuario único, Contraseña con doble confirmación.
 *
 * MP-6: Login con bloqueo por intentos fallidos
 * Como: usuario registrado. Quiero: iniciar sesión. Para: que el sistema me bloquee tras 3 intentos fallidos.
 * Criterios: Contador de intentos visible, bloqueo tras 3 fallos, desbloqueo solo administrativo.
 *
 * MP-7: Cambio de contraseña con fricción intencional
 * Como: cliente autenticado. Quiero: cambiar mi contraseña en 4 pasos seguros.
 * Para: evitar cambios accidentales.
 * Criterios: Validar clave actual → nueva clave → confirmación → persistencia.
 */
@Getter
@Builder
public class Cliente implements IAutenticable {

    private static final int MAX_INTENTOS = 3;

    private final UUID id;
    private final String cedula;
    private final String nombreCompleto;
    private final String celular;
    private final String username;
    private String passwordHash;
    private EstadoCliente estado;
    private int intentosFallidos;

    @Builder.Default
    private List<Cuenta> cuentas = new ArrayList<>();

    @Override
    public void autenticar(boolean passwordCorrecto) {
        if (estado == EstadoCliente.BLOQUEADO)
            throw new CuentaBloqueadaException(
                    "Cliente bloqueado por exceso de intentos fallidos.");

        if (!passwordCorrecto) {
            this.intentosFallidos++;
            if (this.intentosFallidos >= MAX_INTENTOS) {
                this.estado = EstadoCliente.BLOQUEADO;
                throw new CuentaBloqueadaException(
                        "Cuenta bloqueada tras 3 intentos fallidos.");
            }
            throw new CredencialesInvalidasException(
                    "Contraseña incorrecta. Intentos restantes: "
                            + (MAX_INTENTOS - intentosFallidos));
        }
        this.intentosFallidos = 0;
    }

    @Override
    public void cerrarSesion() {
        // La sesión es gestionada por la capa de presentación; el dominio no mantiene estado de sesión
    }

    @Override
    public void cambiarContrasena(boolean actualCorrecto, String hashNuevo) {
        autenticar(actualCorrecto);
        this.passwordHash = hashNuevo;
    }

    public void desbloquear() {
        this.estado = EstadoCliente.ACTIVO;
        this.intentosFallidos = 0;
    }

    public int getIntentosRestantes() {
        return Math.max(0, MAX_INTENTOS - intentosFallidos);
    }

    public List<Cuenta> getCuentas() {
        return Collections.unmodifiableList(cuentas);
    }

    public void agregarCuenta(Cuenta cuenta) {
        this.cuentas.add(cuenta);
    }
}