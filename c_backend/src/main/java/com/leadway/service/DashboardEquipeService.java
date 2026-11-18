package com.leadway.service;

import com.leadway.dto.response.DashboardEquipeIndicadoresResponseDTO;
import com.leadway.dto.response.DashboardEquipeResponseDTO;
import com.leadway.dto.response.DesempenhoVendedorResponseDTO;
import com.leadway.entity.Contato;
import com.leadway.entity.Lead;
import com.leadway.entity.Vendedor;
import com.leadway.exception.VendedorNaoEncontradoException;
import com.leadway.repository.ContatoRepository;
import com.leadway.repository.InteracaoRepository;
import com.leadway.repository.LeadRepository;
import com.leadway.repository.VendedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardEquipeService {
    private final VendedorRepository vendedorRepository;
    private final LeadRepository leadRepository;
    private final ContatoRepository contatoRepository;
    private final InteracaoRepository interacaoRepository;

    private static final int DIAS_PARADO = 7;
    private static final int ORDEM_ETAPA_FECHADA = 5;
    private static final int ORDEM_ETAPA_PROPOSTA = 3;


    public DashboardEquipeService(VendedorRepository vendedorRepository, LeadRepository leadRepository, ContatoRepository contatoRepository, InteracaoRepository interacaoRepository) {
        this.vendedorRepository = vendedorRepository;
        this.leadRepository = leadRepository;
        this.contatoRepository = contatoRepository;
        this.interacaoRepository = interacaoRepository;
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

    @Transactional(readOnly = true)
    public DashboardEquipeIndicadoresResponseDTO buscarIndicadores(Integer liderId){

        if (!vendedorRepository.existsById(liderId)){
            throw new VendedorNaoEncontradoException("Vendedor líder não consta nos registros");
        }

        LocalDate hoje = LocalDate.now();
        LocalDate inicioDaSemana = hoje.minusDays(hoje.getDayOfWeek().getValue() - 1);

        LocalDateTime inicio = inicioDaSemana.atStartOfDay();
        LocalDateTime fim = inicioDaSemana.plusWeeks(1).atStartOfDay();
        LocalDateTime limiteParado = LocalDateTime.now().minusDays(DIAS_PARADO);

        Long representantesAtivos = vendedorRepository.countByLider_IdAndAtivoTrue(liderId);
        Long leadsCarteira = leadRepository.contarCarteiraPorLider(liderId);
        Long contatosRealizados = interacaoRepository.contarPorLiderNoPeriodo(liderId, inicio, fim);
        Long pendenciasEquipe = leadRepository.contarParadosPorLider(liderId, limiteParado, ORDEM_ETAPA_FECHADA);

        return new DashboardEquipeIndicadoresResponseDTO(
                representantesAtivos.intValue(),
                leadsCarteira.intValue(),
                contatosRealizados.intValue(),
                pendenciasEquipe.intValue()
        );
    }

    public List<DesempenhoVendedorResponseDTO> buscarDesempenho(Integer liderId){
        if (!vendedorRepository.existsById(liderId)){
            throw new VendedorNaoEncontradoException("Vendedor líder não encontrado nos registros");
        }

        LocalDateTime inicio = inicioDaSemana();
        LocalDateTime fim = inicio.plusWeeks(1);
        LocalDateTime limiteParado = LocalDateTime.now().minusDays(DIAS_PARADO);

        List<Vendedor> listaVendedores = vendedorRepository.findByLider_IdAndAtivoTrueOrderByNomeAsc(liderId);

        List<DesempenhoVendedorResponseDTO> listaDesempenho = new ArrayList<>();

        for(Vendedor vendedor : listaVendedores){
            Integer id = vendedor.getId();

            Integer leads = leadRepository.countByVendedor_IdAndAtivoTrue(id);
            Long propostas = leadRepository.countByVendedor_IdAndAtivoTrueAndEtapa_Ordem(id, ORDEM_ETAPA_PROPOSTA);
            Long contatos = interacaoRepository.contarVendedorNoPeriodo(id, inicio, fim);
            Long pendencias = leadRepository.contarParadosPorVendedor(id, limiteParado, ORDEM_ETAPA_FECHADA);

            listaDesempenho.add(new DesempenhoVendedorResponseDTO(
                    id,
                    vendedor.getNome(),
                    vendedor.getCodVendedor(),
                    vendedor.getEmail(),
                    leads.intValue(),
                    contatos.intValue(),
                    propostas.intValue(),
                    pendencias.intValue()
            ));

            listaDesempenho.sort(Comparator
                    .comparing(DesempenhoVendedorResponseDTO::getContatos, Comparator.reverseOrder())
                    .thenComparing(DesempenhoVendedorResponseDTO::getNome));

        }
            return listaDesempenho;
    }

    private LocalDateTime inicioDaSemana() {
        LocalDate hoje = LocalDate.now();
        return hoje.minusDays(hoje.getDayOfWeek().getValue() - 1).atStartOfDay();
    }
}
