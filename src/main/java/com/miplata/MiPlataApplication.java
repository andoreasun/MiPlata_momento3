package com.miplata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * EPI 4 – UI/UX | MP-12: Navegación fluida y layout base responsive
 *
 * Como: usuario. Quiero: navegar sin interrupciones desde inicio hasta el panel transaccional.
 * Para: tener una experiencia fluida en cualquier dispositivo.
 *
 * Criterios de aceptación:
 * - Flujo: Inicio → Registro/Login → Panel transaccional
 * - Diseño responsive: Desktop, Tablet y Móvil
 * - Identidad visual 'Mi Plata' coherente (colores y tipografía)
 * - Sistema de alertas: Éxito (verde), Error (rojo), Advertencia (amarillo)
 * - Ninguna sección sin navegación de retorno
 */
@SpringBootApplication
public class MiPlataApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiPlataApplication.class, args);
    }

}
