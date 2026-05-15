package com.miplata.application.dto;

import com.miplata.domain.enums.TipoMovimiento;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MovimientoDto {
    private String id;
    private TipoMovimiento tipo;
    private BigDecimal valor;
    private LocalDateTime fecha;
    private String descripcion;
}