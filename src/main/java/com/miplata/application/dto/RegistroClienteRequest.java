package com.miplata.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroClienteRequest {
    @NotBlank private String cedula;
    @NotBlank private String nombreCompleto;
    @NotBlank private String celular;
    @NotBlank @Size(min = 4) private String username;
    @NotBlank @Size(min = 6) private String password;
    @NotBlank private String confirmPassword;
}