package com.leadway.service;

import com.leadway.dto.request.AtualizarLeadRequestDTO;
import com.leadway.dto.request.CadastroLeadRequestDTO;
import com.leadway.dto.request.TransferirLeadRequestDTO;
import com.leadway.dto.response.CepResponseDTO;
import com.leadway.entity.*;
import com.leadway.exception.*;
import com.leadway.mapper.CadastroLeadMapper;
import com.leadway.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Base do feature/teste-all (vendedor opcional, histórico de etapa, prioridade,
 * soft delete) com transferência, log e ViaCep portados do feature/calcom.
 */
@Service
public class LeadService {

    private final LeadRepository leadRepository;
    private final LeadEtapaHistoricoRepository historicoRepository;
    private final EtapaRepository etapaRepository;
    private final SetorRepository setorRepository;
    private final VendedorRepository vendedorRepository;
    private final CadastroLeadMapper cadastroLeadMapper = new CadastroLeadMapper();
    private final EnderecoRepository enderecoRepository;
    private final LogRepository logRepository;
    private final CepService cepService;

    public LeadService(LeadRepository leadRepository,
                       EtapaRepository etapaRepository,
                       SetorRepository setorRepository,
                       VendedorRepository vendedorRepository,
                       EnderecoRepository enderecoRepository,
                       LeadEtapaHistoricoRepository historicoRepository,
                       LogRepository logRepository,
                       CepService cepService) {
        this.historicoRepository = historicoRepository;
        this.leadRepository = leadRepository;
        this.etapaRepository = etapaRepository;
        this.setorRepository = setorRepository;
        this.vendedorRepository = vendedorRepository;
        this.enderecoRepository = enderecoRepository;
        this.logRepository = logRepository;
        this.cepService = cepService;
    }

    public List<Lead> listarLeads() {
        return leadRepository.findAll();
    }

    public List<Lead> listarPorFiltro(String termo, Integer vendedorId, String fase) {
        return leadRepository.findByFiltros(termo, vendedorId, fase);
    }

    /** Mantido para LeadDetalheService (feature/calcom). */
    public Lead listarId(Integer id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new LeadNaoEncontradoException("Lead não consta nos registros"));
    }

    /** feature/calcom: transferência com log, agora autorizada no controller. */
    @Transactional
    public void transferir(Integer leadId, TransferirLeadRequestDTO dto) {
        Lead lead = listarId(leadId);
        Vendedor vendedor = vendedorRepository.findById(dto.getVendedorId())
                .orElseThrow(() -> new VendedorNaoEncontradoException("Vendedor não consta nos registros"));
        if (!Boolean.TRUE.equals(vendedor.getAtivo()))
            throw new InativoException("Vendedor está inativo");

        boolean responsavelDiferente = lead.getVendedor() == null
                || !lead.getVendedor().getId().equals(vendedor.getId());
        lead.setVendedor(vendedor);
        if (responsavelDiferente) lead.setVinculadoEm(LocalDateTime.now());
        leadRepository.save(lead);

        Log log = new Log();
        log.setTipo("TRANSFERENCIA");
        log.setMensagem(montarMensagemTransferencia(leadId, vendedor.getNome(), dto.getMotivacao()));
        logRepository.save(log);
    }

    private String montarMensagemTransferencia(Integer leadId, String nomeVendedor, String motivacao) {
        StringBuilder mensagem = new StringBuilder("Lead ").append(leadId)
                .append(" transferido para ").append(nomeVendedor);
        if (motivacao != null && !motivacao.isBlank()) mensagem.append(" - ").append(motivacao);
        String texto = mensagem.toString();
        return texto.length() <= 100 ? texto : texto.substring(0, 100);
    }

    public Lead salvar(CadastroLeadRequestDTO dto) {
        if (leadRepository.existsByEmailIgnoreCase(dto.getEmail()))
            throw new EmailJaCadastradoException("Email já cadastrado anteriormente");
        if (leadRepository.existsBycnpj(dto.getCnpj()))
            throw new CNPJJaCadastradoException("CNPJ já consta nos registros");

        Lead cadastro = cadastroLeadMapper.toEntity(dto);
        if (dto.getVendedorId() != null) {
            Optional<Vendedor> vendedorOpt = vendedorRepository.findById(dto.getVendedorId());
            if (vendedorOpt.isEmpty())
                throw new VendedorNaoEncontradoException("Vendedor não encontrado");
            Vendedor vendedor = vendedorOpt.get();
            if (vendedor.getAtivo()) {
                cadastro.setVendedor(vendedor);
                cadastro.setVinculadoEm(LocalDateTime.now());
            }
        }
        if (dto.getSetorId() == null)
            throw new SetorObrigatorioException("Obrigatório informar um setor de mercado");
        Setor setor = setorRepository.findById(dto.getSetorId())
                .orElseThrow(() -> new SetorNaoExistenteException("Setor não encontrado nos registros"));
        cadastro.setSetor(setor);

        Etapa etapa;
        if (dto.getEtapaId() == null) {
            etapa = etapaRepository.findFirstByOrderByIdAsc()
                    .orElseThrow(() -> new EtapaNaoRegistradaException("Etapa não registrada"));
        } else {
            etapa = etapaRepository.findById(dto.getEtapaId())
                    .orElseThrow(() -> new EtapaNaoRegistradaException("Etapa não registrada"));
        }
        cadastro.setEtapa(etapa);

        if (dto.getEndereco() == null)
            throw new EnderecoObrigatorioException("Endereço incompleto. Preencha-o corretamente");

        // feature/calcom: enriquecimento ViaCep, preservando o que o cliente informou.
        CadastroLeadRequestDTO.EnderecoDto enderecoDto = dto.getEndereco();
        String cepNormal = cepService.normalizar(enderecoDto.getCep());
        enderecoDto.setCep(cepNormal);
        preencherEnderecoSeVazio(enderecoDto, cepService.consultar(cepNormal));
        cadastro.setEndereco(cadastroLeadMapper.toEndereco(enderecoDto));

        Endereco enderecoSalvar = enderecoRepository.save(cadastro.getEndereco());
        cadastro.setEndereco(enderecoSalvar);
        Lead leadCadastrar = leadRepository.save(cadastro);
        registrarEtapa(leadCadastrar);
        return leadCadastrar;
    }

    private void preencherEnderecoSeVazio(CadastroLeadRequestDTO.EnderecoDto endDto, CepResponseDTO cepInfo) {
        if (isBlank(endDto.getRua())) endDto.setRua(cepInfo.getRua());
        if (isBlank(endDto.getBairro())) endDto.setBairro(cepInfo.getBairro());
        if (isBlank(endDto.getCidade())) endDto.setCidade(cepInfo.getCidade());
        if (isBlank(endDto.getEstado())) endDto.setEstado(cepInfo.getEstado());
        if (isBlank(endDto.getPais())) endDto.setPais(cepInfo.getPais());
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public Lead atualizar(Integer id, AtualizarLeadRequestDTO dto) {
        if (dto == null) throw new LeadVazioException("Os campos estão vazios");
        Lead lead = listarId(id);
        lead.setId(id);

        if (dto.getEmail() != null) {
            if (!dto.getEmail().equalsIgnoreCase(lead.getEmail())
                    && leadRepository.existsByEmailIgnoreCase(dto.getEmail()))
                throw new EmailJaCadastradoException("Email já cadastrado anteriormente");
            lead.setEmail(dto.getEmail());
        }
        if (dto.getTelefone() != null) lead.setTelefone(dto.getTelefone());
        if (dto.getPrioridade() != null) {
            if (!Set.of("ALTA", "MEDIA", "BAIXA").contains(dto.getPrioridade()))
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST, "Prioridade inválida");
            lead.setPrioridade(dto.getPrioridade());
        }
        if (dto.getPotencial() != null) lead.setPotencial(dto.getPotencial());
        if (dto.getDataPrimeiroContato() != null) lead.setDataPrimeiroContato(dto.getDataPrimeiroContato());
        if (dto.getDataPrimeiraVisita() != null) {
            if (lead.getDataPrimeiroContato() != null && dto.getDataPrimeiraVisita().isBefore(lead.getDataPrimeiroContato()))
                throw new DataInvalidaException("Data informada não pode ser anterior à data do primeiro contato");
            lead.setDataPrimeiraVisita(dto.getDataPrimeiraVisita());
        }
        if (dto.getMotivoPerda() != null) lead.setMotivoPerda(dto.getMotivoPerda());
        if (dto.getAtivo() != null) {
            if (!dto.getAtivo() && (lead.getMotivoPerda() == null || lead.getMotivoPerda().isBlank()))
                throw new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.BAD_REQUEST, "Motivo da perda obrigatório");
            lead.setAtivo(dto.getAtivo());
        }

        if (dto.getEndereco() != null) {
            Endereco endereco = lead.getEndereco();
            AtualizarLeadRequestDTO.EnderecoDto enderecoDto = dto.getEndereco();
            if (enderecoDto.getCep() != null) {
                String cepNormal = cepService.normalizar(enderecoDto.getCep());
                enderecoDto.setCep(cepNormal);
                endereco.setCep(cepNormal);
                CepResponseDTO cepInfo = cepService.consultar(cepNormal);
                if (enderecoDto.getRua() == null) endereco.setRua(cepInfo.getRua());
                if (enderecoDto.getBairro() == null) endereco.setBairro(cepInfo.getBairro());
                if (enderecoDto.getCidade() == null) endereco.setCidade(cepInfo.getCidade());
                if (enderecoDto.getEstado() == null) endereco.setEstado(cepInfo.getEstado());
                // AtualizarLeadRequestDTO.EnderecoDto não expõe pais: preserva o valor já cadastrado.
                if (endereco.getPais() == null) endereco.setPais(cepInfo.getPais());
            }
            if (enderecoDto.getRua() != null) endereco.setRua(enderecoDto.getRua());
            if (enderecoDto.getComplemento() != null) endereco.setComplemento(enderecoDto.getComplemento());
            if (enderecoDto.getNumero() != null) endereco.setNumero(enderecoDto.getNumero());
            if (enderecoDto.getBairro() != null) endereco.setBairro(enderecoDto.getBairro());
            if (enderecoDto.getCidade() != null) endereco.setCidade(enderecoDto.getCidade());
            if (enderecoDto.getEstado() != null) endereco.setEstado(enderecoDto.getEstado());
        }

        if (dto.getVendedorId() != null) {
            Vendedor vendedor = vendedorRepository.findById(dto.getVendedorId())
                    .orElseThrow(() -> new VendedorNaoEncontradoException("Vendedor não consta nos registros"));
            if (!vendedor.getAtivo()) throw new InativoException("Vendedor solicitado está inativo");
            if (lead.getVendedor() == null || !lead.getVendedor().getId().equals(vendedor.getId()))
                lead.setVinculadoEm(LocalDateTime.now());
            lead.setVendedor(vendedor);
        }
        if (dto.getEtapaId() != null) {
            Etapa etapa = etapaRepository.findById(dto.getEtapaId())
                    .orElseThrow(() -> new EtapaNaoRegistradaException("Etapa não consta nos registros"));
            if (lead.getEtapa() == null || !lead.getEtapa().getId().equals(etapa.getId())) {
                lead.setEtapa(etapa);
                registrarEtapa(lead);
            }
        }
        return leadRepository.save(lead);
    }

    /** Última entrada registrada na etapa atual. Não inventa datas para históricos ausentes. */
    public LocalDateTime obterEntradaEtapaAtual(Integer leadId) {
        Lead lead = listarId(leadId);
        if (lead.getEtapa() == null) return null;
        return historicoRepository
                .findFirstByLeadIdAndEtapaIdOrderByCriadoEmDescIdDesc(leadId, lead.getEtapa().getId())
                .map(LeadEtapaHistorico::getCriadoEm)
                .orElse(null);
    }

    /** Único dono da escrita do histórico de etapa (compartilhado com InteracaoService). */
    public void registrarEtapa(Lead lead) {
        if (lead.getEtapa() == null) return;
        LeadEtapaHistorico historico = new LeadEtapaHistorico();
        historico.setLead(lead);
        historico.setEtapa(lead.getEtapa());
        historicoRepository.save(historico);
    }

    public void apagar(Integer id) {
        Lead lead = listarId(id);
        if (lead.getMotivoPerda() == null || lead.getMotivoPerda().isBlank())
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Informe o motivo da perda antes de desativar");
        lead.setAtivo(false);
        leadRepository.save(lead);
    }
}
