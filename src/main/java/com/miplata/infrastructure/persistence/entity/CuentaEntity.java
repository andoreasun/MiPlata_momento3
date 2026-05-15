package com.miplata.infrastructure.persistence.entity;

import com.miplata.domain.enums.EstadoCuenta;
import com.miplata.domain.enums.TipoCuenta;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "cuentas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaEntity {
    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String numeroCuenta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCuenta tipoCuenta;

    @Column(precision = 15, scale = 2)
    private BigDecimal saldo;

    @Column(precision = 15, scale = 2)
    private BigDecimal cupoTotal;

    @Column(precision = 15, scale = 2)
    private BigDecimal cupoDisponible;

    @Enumerated(EnumType.STRING)
    private EstadoCuenta estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;

    @OneToMany(mappedBy = "cuenta",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<MovimientoEntity> movimientos = new ArrayList<>();
}