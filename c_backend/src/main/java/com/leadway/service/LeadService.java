package com.leadway.service;

import com.leadway.dto.request.AtualizarLeadRequestDTO;
import com.leadway.dto.request.AtualizarSenhaRequestDTO;
import com.leadway.dto.request.CadastroLeadRequestDTO;
import com.leadway.dto.request.TransferirLeadRequestDTO;
import com.leadway.dto.response.CepResponseDTO;
import com.leadway.entity.*;
import com.leadway.exception.*;
import com.leadway.mapper.CadastroLeadMapper;
import com.leadway.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeadService {

    private final LeadRepository leadRepository;
    private final EtapaRepository etapaRepository;
    private final SetorRepository setorRepository;
    private final VendedorRepository vendedorRepository;
    private final CadastroLeadMapper cadastroLeadMapper = new CadastroLeadMapper();
    private final EnderecoRepository enderecoRepository;
    private final LogRepository logRepository;
    private final CepService cepService;

    public LeadService(LeadRepository leadRepository, EtapaRepository etapaRepository, SetorRepository setorRepository, VendedorRepository vendedorRepository, EnderecoRepository enderecoRepository, LogRepository logRepository, CepService cepService) {
        this.leadRepository = leadRepository;
        this.etapaRepository = etapaRepository;
        this.setorRepository = setorRepository;
        this.vendedorRepository = vendedorRepository;
        this.enderecoRepository = enderecoRepository;
        this.logRepository = logRepository;
        this.cepService = cepService;
    }

    public List<Lead> listarLeads() {
        List<Lead> lista = leadRepository.findAll();
        return lista;
    }

    public List<Lead> listarPorFiltro(String termo, Integer vendedorId, String fase) {
        List<Lead> lista = leadRepository.findByFiltros(termo, vendedorId, fase);
        return lista;
    }

    public Lead listarId(Integer id){
        Optional<Lead> leadOpt = leadRepository.findById(id);
        if (leadOpt.isEmpty()){
            throw new LeadNaoEncontradoException("Lead não consta nos registros");
        }
        Lead lead = leadOpt.get();
        return lead;
    }

    public void atualizarLead(Integer leadId, TransferirLeadRequestDTO dto){
        Optional<Lead> leadOpt = leadRepository.findById(leadId);
        if (leadOpt.isEmpty()){
            throw new LeadNaoEncontradoException("Lead não consta nos registros");
        }
        Lead lead = leadOpt.get();
        Optional<Vendedor> vendedorOpt = vendedorRepository.findById(dto.getVendedorId());
        if (vendedorOpt.isEmpty()){
            throw new VendedorNaoEncontradoException("Vendedor não consta nos registros");
        }
        Vendedor vendedor = vendedorOpt.get();
        if (!vendedor.getAtivo()){
            throw new InativoException("Vendedor está inativo");
        }
        lead.setVendedor(vendedor);

        Log log = new Log();
        log.setTipo("TRANSFERENCIA");
        log.setMensagem("Lead " + leadId + " tranferido para " + vendedor.getNome() + " - " + dto.getMotivacao());
        logRepository.save(log);
    }

    public Lead salvar(CadastroLeadRequestDTO dto) {
        if (leadRepository.existsByEmailIgnoreCase(dto.getEmail())){
            throw new EmailJaCadastradoException("Email já cadastrado anteriormente");
        }
        if (leadRepository.existsBycnpj(dto.getCnpj())){
            throw new CNPJJaCadastradoException("CNPJ já consta nos registros");
        }
        Lead cadastro = cadastroLeadMapper.toEntity(dto);
        if (dto.getVendedorId() == null) {
            throw new VendedorNaoEncontradoException("Vendedor responsável é obrigatório");
        }
        Optional<Vendedor> vendedorOpt = vendedorRepository.findById(dto.getVendedorId());
        if (vendedorOpt.isEmpty()) {
            throw new VendedorNaoEncontradoException("Vendedor não encontrado");
        }
        Vendedor vendedor = vendedorOpt.get();
        if (!vendedor.getAtivo()) {
            throw new InativoException("Vendedor responsável está inativo");
        }
        cadastro.setVendedor(vendedor);
        if (dto.getSetorId() == null){
            throw new SetorObrigatorioException("Obrigatório informar um setor de mercado");
        }
        Optional<Setor> setorOpt = setorRepository.findById(dto.getSetorId());
        if (setorOpt.isEmpty()) {
            throw new SetorNaoExistenteException("Setor não encontrado nos registros");
        }
        Setor setor = setorOpt.get();
        cadastro.setSetor(setor);
        Etapa etapa;
        if (dto.getEtapaId() == null) {
            Optional<Etapa> etapaOpt = etapaRepository.findFirstByOrderByIdAsc();
            if (etapaOpt.isEmpty()) {
                throw new EtapaNaoRegistradaException("Etapa não registrada");
            }
            etapa = etapaOpt.get();
            cadastro.setEtapa(etapa);
        } else {
            Optional<Etapa> etapaOpt = etapaRepository.findById(dto.getEtapaId());
            if (etapaOpt.isEmpty()) {
                throw new EtapaNaoRegistradaException("Etapa não registrada");
            }
            etapa = etapaOpt.get();
            cadastro.setEtapa(etapa);
        }
        if (dto.getEndereco() == null) {
            throw new EnderecoObrigatorioException("Endereço incompleto. Preencha-o corretamente");
        }
        CadastroLeadRequestDTO.EnderecoDto enderecoDto = dto.getEndereco();
        String cepNormal = cepService.normalizar(enderecoDto.getCep());
        enderecoDto.setCep(cepNormal);
        CepResponseDTO cepResponseDTO = cepService.consultar(cepNormal);
        preencherEnderecoSeVazio(enderecoDto, cepResponseDTO);

        cadastro.setEndereco(cadastroLeadMapper.toEndereco(enderecoDto));
        Endereco enderecoSalvar = enderecoRepository.save(cadastro.getEndereco());
        cadastro.setEndereco(enderecoSalvar);
        Lead leadCadastrar = leadRepository.save(cadastro);
        return leadCadastrar;
    }

    private void preencherEnderecoSeVazio(CadastroLeadRequestDTO.EnderecoDto endDto, CepResponseDTO cepInfo) {
        if (isBlank(endDto.getRua())){
            endDto.setRua(cepInfo.getRua());
        }
        if (isBlank(endDto.getBairro())){
            endDto.setBairro(cepInfo.getBairro());
        }
        if (isBlank(endDto.getCidade())){
            endDto.setCidade(cepInfo.getCidade());
        }
        if (isBlank(endDto.getEstado())){
            endDto.setEstado(cepInfo.getEstado());
        }
        if (isBlank(endDto.getPais())){
            endDto.setPais(cepInfo.getPais());
        }
    }
    private boolean isBlank(String v) { return v == null || v.trim().isEmpty(); }

    public Lead atualizar(Integer id, AtualizarLeadRequestDTO dto) {
        if (dto == null){
            throw new LeadVazioException("Os campos estão vazios");
        }
        Optional<Lead> leadOpt = leadRepository.findById(id);
        if (leadOpt.isEmpty()){
            throw new LeadNaoEncontradoException("Lead não consta nos registros");
        }
        Lead lead = leadOpt.get();
        lead.setId(id);
        if (dto.getEmail() != null) {
            if (!dto.getEmail().equalsIgnoreCase(lead.getEmail())) {
                if (leadRepository.existsByEmailIgnoreCase(dto.getEmail())) {
                    throw new EmailJaCadastradoException("Email já cadastrado anteriormente");
                }
            }
            lead.setEmail(dto.getEmail());
        }
        if (dto.getTelefone() != null){
            lead.setTelefone(dto.getTelefone());
        }
        if (dto.getPotencial() != null){
            lead.setPotencial(dto.getPotencial());
        }
        if (dto.getPrioridade() != null){
            lead.setPrioridade(dto.getPrioridade());
        }
        if (dto.getDataPrimeiroContato() != null){
            lead.setDataPrimeiroContato(dto.getDataPrimeiroContato());
        }
        if (dto.getDataPrimeiraVisita() != null){
            if (dto.getDataPrimeiraVisita().isBefore(lead.getDataPrimeiroContato())){
                throw new DataInvalidaException("Data informada não pode ser anterior à data do primeiro contato");
            }
            lead.setDataPrimeiraVisita(dto.getDataPrimeiraVisita());
        }
        if (dto.getMotivoPerda() != null){
            lead.setMotivoPerda(dto.getMotivoPerda());
        }
        if (dto.getAtivo() != null){
            lead.setAtivo(dto.getAtivo());
        }
        if (dto.getEndereco() != null){
            Endereco endereco = lead.getEndereco();
            AtualizarLeadRequestDTO.EnderecoDto enderecoDto = dto.getEndereco();

            CepResponseDTO cepResponseDTO = null;
            if (enderecoDto.getCep() != null){
                String cepNormal = cepService.normalizar(enderecoDto.getCep());
                enderecoDto.setCep(cepNormal);
                cepResponseDTO = cepService.consultar(cepNormal);
                endereco.setCep(cepNormal);

                if (enderecoDto.getRua() == null && !isBlank(cepResponseDTO.getRua())) endereco.setRua(cepResponseDTO.getRua());
                if (enderecoDto.getBairro() == null && !isBlank(cepResponseDTO.getBairro())) endereco.setBairro(cepResponseDTO.getBairro());
                if (enderecoDto.getCidade() == null && !isBlank(cepResponseDTO.getCidade())) endereco.setCidade(cepResponseDTO.getCidade());
                if (enderecoDto.getEstado() == null && !isBlank(cepResponseDTO.getEstado())) endereco.setEstado(cepResponseDTO.getEstado());
            }
            if (enderecoDto.getRua() != null){
                endereco.setRua(enderecoDto.getRua());
            }
            if (enderecoDto.getComplemento() != null){
                endereco.setComplemento(enderecoDto.getComplemento());
            }
            if (enderecoDto.getNumero() != null){
                endereco.setNumero(enderecoDto.getNumero());
            }
            if (enderecoDto.getBairro() != null){
                endereco.setBairro(enderecoDto.getBairro());
            }
            if (enderecoDto.getCidade() != null){
                endereco.setCidade(enderecoDto.getCidade());
            }
            if (enderecoDto.getEstado() != null){
                endereco.setEstado(enderecoDto.getEstado());
            }
        }
        if (dto.getVendedorId() != null) {
            Optional<Vendedor> vendedorOpt =
                    vendedorRepository.findById(dto.getVendedorId());
            if (vendedorOpt.isEmpty()) {
                throw new VendedorNaoEncontradoException("Vendedor não consta nos registros");
            }
            Vendedor vendedor = vendedorOpt.get();
            if (!vendedor.getAtivo()){
                throw new InativoException("Vendedor solicitado está inativo");
            }
            lead.setVendedor(vendedor);
        }
        if (dto.getEtapaId() != null){
            Optional<Etapa> etapaOpt = etapaRepository.findById(dto.getEtapaId());
            if (etapaOpt.isEmpty()){
                throw new EtapaNaoRegistradaException("Etapa não consta nos registros");
            }
            Etapa etapa = etapaOpt.get();
            lead.setEtapa(etapa);
        }
        Lead cadastradoNovo = leadRepository.save(lead);
        return cadastradoNovo;
    }

    public void apagar(Integer id){
        if (!leadRepository.existsById(id)){
            throw new LeadNaoEncontradoException("Lead não consta nos registros");
        }
        leadRepository.deleteById(id);
    }
}
