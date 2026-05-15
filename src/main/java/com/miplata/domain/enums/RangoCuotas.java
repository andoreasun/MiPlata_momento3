package com.miplata.domain.enums;

import java.math.BigDecimal;

public enum RangoCuotas {
    SIN_INTERES(1, 2, BigDecimal.ZERO),
    TASA_BAJA(3, 6, new BigDecimal("0.019")),
    TASA_ALTA(7, Integer.MAX_VALUE, new BigDecimal("0.023"));

    private final int minCuotas;
    private final int maxCuotas;
    private final BigDecimal tasaMensual;

    RangoCuotas(int min, int max, BigDecimal tasa) {
        this.minCuotas = min;
        this.maxCuotas = max;
        this.tasaMensual = tasa;
    }

    public static BigDecimal tasaPara(int cuotas) {
        for (RangoCuotas r : values()) {
            if (cuotas >= r.minCuotas && cuotas <= r.maxCuotas)
                return r.tasaMensual;
        }
        throw new IllegalArgumentException("Número de cuotas inválido: " + cuotas);
    }
}