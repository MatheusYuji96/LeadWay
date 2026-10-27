package com.leadway.service;

import com.leadway.dto.request.AtualizarLeadRequestDTO;
import com.leadway.dto.request.CadastroLeadRequestDTO;
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

    public LeadService(LeadRepository leadRepository, EtapaRepository etapaRepository, SetorRepository setorRepository, VendedorRepository vendedorRepository, EnderecoRepository enderecoRepository) {
        this.leadRepository = leadRepository;
        this.etapaRepository = etapaRepository;
        this.setorRepository = setorRepository;
        this.vendedorRepository = vendedorRepository;
        this.enderecoRepository = enderecoRepository;
    }

    public List<Lead> listarLeads() {
        List<Lead> lista = leadRepository.findAll();
        return lista;
    }

    public List<Lead> listarPorFiltro(String termo, Integer vendedorId, String fase) {
        List<Lead> lista = leadRepository.findByFiltros(termo, vendedorId, fase);
        return lista;
    }

    public Lead salvar(CadastroLeadRequestDTO dto) {
        Lead cadastro = cadastroLeadMapper.toEntity(dto);
        if (leadRepository.existsByEmailIgnoreCase(dto.getEmail())){
            throw new EmailJaCadastradoException("Email já cadastrado anteriormente");
        }
        if (leadRepository.existsBycnpj(dto.getCnpj())){
            throw new CNPJJaCadastradoException("CNPJ já consta nos registros");
        }
        if (dto.getVendedorId() != null) {
            Optional<Vendedor> vendedorOpt = vendedorRepository.findById(dto.getVendedorId());
            if (vendedorOpt.isEmpty()) {
                throw new VendedorNaoEncontradoException("Vendedor não encontrado");
            }
            Vendedor vendedor = vendedorOpt.get();
            cadastro.setVendedor(vendedor);
            cadastro.setVinculadoEm(java.time.LocalDateTime.now());
        }
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
        Endereco enderecoSalvar = enderecoRepository.save(cadastro.getEndereco());
        cadastro.setEndereco(enderecoSalvar);
        Lead leadCadastrar = leadRepository.save(cadastro);
        return leadCadastrar;
    }

    public Lead atualizar(Integer id, AtualizarLeadRequestDTO dto) {
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
        if (dto.getDataPrimeiroContato() != null){
            lead.setDataPrimeiroContato(dto.getDataPrimeiroContato());
        }
        if (dto.getDataPrimeiraVisita() != null){
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

            if (enderecoDto.getRua() != null){
                endereco.setRua(enderecoDto.getRua());
            }
            if (enderecoDto.getCep() != null){
                endereco.setCep(enderecoDto.getCep());
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

            if (lead.getVendedor() == null || !lead.getVendedor().getId().equals(vendedor.getId())) {
                lead.setVinculadoEm(java.time.LocalDateTime.now());
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
