package com.leadway.mapper;

import com.leadway.dto.request.CadastroVendedorRequestDTO;
import com.leadway.dto.response.VendedorResponseDTO;
import com.leadway.entity.Vendedor;

public class VendedorMapper {

    public static Vendedor toEntity(CadastroVendedorRequestDTO dto) {
        Vendedor vendedor = new Vendedor();
        vendedor.setNome(dto.getNome());
        vendedor.setEmail(dto.getEmail());
        vendedor.setSenha(dto.getSenha());
        vendedor.setTelefone(dto.getTelefone());

        return vendedor;
    }

    public static VendedorResponseDTO toResponse(Vendedor vendedor) {
        VendedorResponseDTO dto = new VendedorResponseDTO();

        dto.setId(vendedor.getId());
        dto.setNome(vendedor.getNome());
        dto.setEmail(vendedor.getEmail());
        dto.setTelefone(vendedor.getTelefone());

        return dto;
    }
}
