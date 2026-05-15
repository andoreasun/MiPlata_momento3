package com.miplata.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String clienteId;
    private String nombreCompleto;
    private int intentosRestantes;
    private boolean autenticado;
    private String mensaje;
}