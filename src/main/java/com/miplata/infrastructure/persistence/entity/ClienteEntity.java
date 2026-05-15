package com.miplata.infrastructure.persistence.entity;

import com.miplata.domain.enums.EstadoCliente;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "clientes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEntity {
    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String cedula;

    @Column(nullable = false)
    private String nombreCompleto;

    private String celular;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private EstadoCliente estado;

    private int intentosFallidos;

    @OneToMany(mappedBy = "cliente",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    @Builder.Default
    private List<CuentaEntity> cuentas = new ArrayList<>();
}