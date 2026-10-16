package com.leadway.mapper;

import com.leadway.dto.request.CadastroVendedorRequestDTO;
import com.leadway.dto.response.CadastroVendedorResponseDTO;
import com.leadway.entity.Vendedor;

public class CadastroVendedorMapper {

    public Vendedor toEntity(CadastroVendedorRequestDTO dto) {
        Vendedor vendedor = new Vendedor();
        vendedor.setNome(dto.getNome());
        vendedor.setEmail(dto.getEmail());
        vendedor.setSenha(dto.getSenha());
        vendedor.setTelefone(dto.getTelefone());

        return vendedor;
    }

    public CadastroVendedorResponseDTO toResponse(Vendedor vendedor) {
        return new CadastroVendedorResponseDTO(
                vendedor.getId(),
                vendedor.getNome(),
                vendedor.getEmail(),
                vendedor.getTelefone()
        );
    }
}
