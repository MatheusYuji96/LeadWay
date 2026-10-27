package com.leadway.service;

import com.leadway.dto.response.DashboardEquipeResponseDTO;
import com.leadway.entity.Contato;
import com.leadway.entity.Lead;
import com.leadway.entity.Vendedor;
import com.leadway.repository.ContatoRepository;
import com.leadway.repository.LeadRepository;
import com.leadway.repository.VendedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardEquipeService {
    private final VendedorRepository vendedorRepository;
    private final LeadRepository leadRepository;
    private final ContatoRepository contatoRepository;

    public DashboardEquipeService(VendedorRepository vendedorRepository, LeadRepository leadRepository,
                                  ContatoRepository contatoRepository) {
        this.vendedorRepository = vendedorRepository;
        this.leadRepository = leadRepository;
        this.contatoRepository = contatoRepository;
    }

    @Transactional(readOnly = true)
    public DashboardEquipeResponseDTO buscarResumo() {
        List<Vendedor> vendedores = vendedorRepository.findAll();
        List<Lead> leads = leadRepository.findAll().stream()
                .filter(lead -> Boolean.TRUE.equals(lead.getAtivo())).toList();
        Map<Integer, Long> contatosPorLead = contatoRepository.findAll().stream()
                .filter(contato -> contato.getLead() != null && contato.getLead().getId() != null)
                .collect(Collectors.groupingBy(contato -> contato.getLead().getId(), Collectors.counting()));

        LocalDateTime agora = LocalDateTime.now();
        List<DashboardEquipeResponseDTO.LeadParadoDTO> parados = new ArrayList<>();
        for (Lead lead : leads) {
            LocalDateTime referencia = lead.getAtualizadoEm() != null ? lead.getAtualizadoEm() : lead.getCriadoEm();
            if (referencia == null || !referencia.isBefore(agora.minusDays(7))) continue;
            parados.add(new DashboardEquipeResponseDTO.LeadParadoDTO(
                    lead.getId(), lead.getNomeFantasia(),
                    lead.getVendedor() != null ? lead.getVendedor().getNome() : "Sem responsável",
                    lead.getEtapa() != null ? lead.getEtapa().getNome() : "Sem etapa",
                    ChronoUnit.DAYS.between(referencia, agora)));
        }
        List<DashboardEquipeResponseDTO.DesempenhoVendedorDTO> desempenho = new ArrayList<>();
        List<DashboardEquipeResponseDTO.RepresentanteResumoDTO> representantes = new ArrayList<>();
        for (Vendedor vendedor : vendedores) {
            if (!Boolean.TRUE.equals(vendedor.getAtivo())) continue;
            List<Lead> carteira = leads.stream().filter(lead -> lead.getVendedor() != null
                    && vendedor.getId().equals(lead.getVendedor().getId())).toList();
            int contatos = carteira.stream().mapToInt(lead -> contatosPorLead.getOrDefault(lead.getId(), 0L).intValue()).sum();
            int propostas = (int) carteira.stream().filter(lead -> lead.getEtapa() != null
                    && Integer.valueOf(3).equals(lead.getEtapa().getOrdem())).count();
            int pendencias = (int) carteira.stream().filter(lead -> parados.stream()
                    .anyMatch(parado -> parado.id().equals(lead.getId()))).count();
            desempenho.add(new DashboardEquipeResponseDTO.DesempenhoVendedorDTO(
                    vendedor.getId(), vendedor.getNome(), carteira.size(), contatos, propostas, pendencias));
            representantes.add(new DashboardEquipeResponseDTO.RepresentanteResumoDTO(
                    vendedor.getId(), vendedor.getNome(), vendedor.getCodVendedor(), vendedor.getEmail(), pendencias));
        }
        return new DashboardEquipeResponseDTO(
                representantes.size(), leads.size(),
                desempenho.stream().mapToInt(DashboardEquipeResponseDTO.DesempenhoVendedorDTO::propostas).sum(),
                parados.size(), desempenho, representantes, parados);
    }
}
