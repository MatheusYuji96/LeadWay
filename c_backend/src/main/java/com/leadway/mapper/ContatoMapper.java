package com.leadway.mapper;

import com.leadway.dto.request.ContatoRequestDTO;
import com.leadway.dto.response.ContatoResponseDTO;
import com.leadway.entity.Contato;
import org.springframework.stereotype.Component;

@Component
public class ContatoMapper {

    public Contato toEntity(ContatoRequestDTO dto){
        Contato contato = new Contato();
        contato.setNome(dto.getNome());
        contato.setCargo(dto.getCargo());
        contato.setEmail(dto.getEmail());
        contato.setWhatsapp(dto.getWhatsapp());
        contato.setClassificacao(dto.getContato());
        contato.setAtivo(true);
        return contato;
    }

    public ContatoResponseDTO toResponse(Contato contato){
        ContatoResponseDTO dto = new ContatoResponseDTO();
        dto.setId(contato.getId());
        dto.setLeadId(contato.getLead() == null ? null : contato.getLead().getId());
        dto.setNome(contato.getNome());
        dto.setCargo(contato.getCargo());
        dto.setEmail(contato.getEmail());
        dto.setWhatsapp(contato.getWhatsapp());
        dto.setContato(contato.getClassificacao());
        dto.setAtivo(contato.getAtivo());
        return dto;
    }
}
