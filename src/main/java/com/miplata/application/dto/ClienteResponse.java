package com.miplata.application.dto;

import com.miplata.domain.enums.EstadoCliente;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ClienteResponse {
    private String id;
    private String cedula;
    private String nombreCompleto;
    private String celular;
    private String username;
    private EstadoCliente estado;
    private int intentosFallidos;
    private List<CuentaResponse> cuentas;
}