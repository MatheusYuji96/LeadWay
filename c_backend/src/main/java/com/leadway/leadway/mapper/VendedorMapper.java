package com.leadway.leadway.mapper;

import com.leadway.leadway.dto.request.VendedorRequestDTO;
import com.leadway.leadway.dto.response.VendedorResponseDTO;
import com.leadway.leadway.entity.Vendedor;
import org.springframework.stereotype.Component;

@Component
public class VendedorMapper {

    public Vendedor toEntity(VendedorRequestDTO dto){
        Vendedor vendedor = new Vendedor();
        vendedor.setNome(dto.getNome());
        vendedor.setCodVendedor(dto.getCodVendedor());
        vendedor.setEmail(dto.getEmail());
        vendedor.setTelefone(dto.getTelefone());
        vendedor.setSenha(dto.getSenha());
        vendedor.setAtivo(true);
        return vendedor;
    }

    public VendedorResponseDTO toResponse(Vendedor vendedor){
        VendedorResponseDTO dto = new VendedorResponseDTO();
        dto.setNome(vendedor.getNome());
        dto.setCodVendedor(vendedor.getCodVendedor());
        dto.setEmail(vendedor.getEmail());
        dto.setTelefone(vendedor.getTelefone());
        dto.setLider(vendedor.getLider());
        dto.setAtivo(vendedor.getAtivo());
        return dto;
    }
}
