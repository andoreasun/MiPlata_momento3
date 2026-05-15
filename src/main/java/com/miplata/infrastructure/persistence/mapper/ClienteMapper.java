package com.miplata.infrastructure.persistence.mapper;

import com.miplata.domain.model.Cliente;
import com.miplata.infrastructure.persistence.entity.ClienteEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteMapper {

    private final CuentaMapper cuentaMapper;

    public Cliente toDomain(ClienteEntity e) {
        Cliente cliente = Cliente.builder()
                .id(e.getId())
                .cedula(e.getCedula())
                .nombreCompleto(e.getNombreCompleto())
                .celular(e.getCelular())
                .username(e.getUsername())
                .passwordHash(e.getPasswordHash())
                .estado(e.getEstado())
                .intentosFallidos(e.getIntentosFallidos())
                .build();

        if (e.getCuentas() != null)
            e.getCuentas().forEach(ce ->
                    cliente.agregarCuenta(cuentaMapper.toDomain(ce)));
        return cliente;
    }

    public ClienteEntity toEntity(Cliente d) {
        return ClienteEntity.builder()
                .id(d.getId())
                .cedula(d.getCedula())
                .nombreCompleto(d.getNombreCompleto())
                .celular(d.getCelular())
                .username(d.getUsername())
                .passwordHash(d.getPasswordHash())
                .estado(d.getEstado())
                .intentosFallidos(d.getIntentosFallidos())
                .build();
    }
}