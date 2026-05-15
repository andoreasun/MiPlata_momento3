package com.miplata.infrastructure.persistence.entity;

import com.miplata.domain.enums.TipoMovimiento;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "movimientos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoEntity {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipo;

    @Column(precision = 15, scale = 2)
    private BigDecimal valor;

    private LocalDateTime fecha;
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id")
    private CuentaEntity cuenta;
}
