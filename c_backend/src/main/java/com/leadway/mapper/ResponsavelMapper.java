package com.leadway.mapper;

import com.leadway.dto.response.ResponsavelResponseDTO;
import com.leadway.entity.Lead;
import com.leadway.entity.Vendedor;
import com.leadway.repository.LeadRepository;

public class ResponsavelMapper {

    private final LeadRepository leadRepository;

    public ResponsavelMapper(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    public ResponsavelResponseDTO toResponsavel(Lead lead) {
        Vendedor responsavel = lead.getVendedor();
        if (responsavel == null) {
            return null;
        }
        ResponsavelResponseDTO dto = new ResponsavelResponseDTO();
        dto.setId(responsavel.getId());
        dto.setNome(responsavel.getNome());
        dto.setEmail(responsavel.getEmail());
        dto.setTelefone(responsavel.getTelefone());
        dto.setIniciais(iniciais(responsavel.getNome()));
        dto.setDesde(lead.getCriadoEm() == null ? null : lead.getCriadoEm().toLocalDate().toString());
        Integer total = leadRepository.countByVendedor_IdAndAtivoTrue(responsavel.getId());
        dto.setLeads(total == null ? 0 : total);
        return dto;
    }

    private String iniciais(String nome) {
        if (nome == null || nome.isBlank()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String parte : nome.trim().split("\\s+")) {
            if (!parte.isEmpty()) {
                sb.append(Character.toUpperCase(parte.charAt(0)));
            }
            if (sb.length() == 2) {
                break;
            }
        }
        return sb.toString();
    }
}
