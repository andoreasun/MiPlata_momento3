package com.miplata.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CambioPasswordRequest {
    @NotBlank private String username;
    @NotBlank private String passwordActual;
    @NotBlank private String passwordNuevo;
    @NotBlank private String confirmPasswordNuevo;
}