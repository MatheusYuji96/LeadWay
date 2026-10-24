package com.leadway.service;

import com.leadway.dto.request.CadastroLeadRequestDTO;
import com.leadway.entity.*;
import com.leadway.exception.*;
import com.leadway.mapper.CadastroLeadMapper;
import com.leadway.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CadastroLeadService {

    private final CadastroLeadRepository cadastroLeadRepository;
    private final EtapaRepository etapaRepository;
    private final SetorRepository setorRepository;
    private final VendedorRepository vendedorRepository;
    private final CadastroLeadMapper cadastroLeadMapper = new CadastroLeadMapper();
    private final EnderecoRepository enderecoRepository;

    public CadastroLeadService(CadastroLeadRepository cadastroLeadRepository, EtapaRepository etapaRepository, SetorRepository setorRepository, VendedorRepository vendedorRepository, EnderecoRepository enderecoRepository) {
        this.cadastroLeadRepository = cadastroLeadRepository;
        this.etapaRepository = etapaRepository;
        this.setorRepository = setorRepository;
        this.vendedorRepository = vendedorRepository;
        this.enderecoRepository = enderecoRepository;
    }

    public List<Lead> listarLeads() {
        List<Lead> lista = cadastroLeadRepository.findAll();
        return lista;
    }

    public List<Lead> listarPorFiltro(String termo, Integer vendedorId, String fase) {
        List<Lead> lista = cadastroLeadRepository.findByFiltros(termo, vendedorId, fase);
        return lista;
    }

    public Lead salvar(CadastroLeadRequestDTO dto) {
        Lead cadastro = cadastroLeadMapper.toEntity(dto);
        if (dto.getVendedorId() != null) {
            Optional<Vendedor> vendedorOpt = vendedorRepository.findById(dto.getVendedorId());
            if (vendedorOpt.isEmpty()) {
                throw new VendedorNaoEncontradoException("Vendedor não encontrado");
            }
            Vendedor vendedor = vendedorOpt.get();
            cadastro.setVendedor(vendedor);
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
        Lead leadCadastrar = cadastroLeadRepository.save(cadastro);
        return leadCadastrar;
    }

    public Lead atualizar(Integer id, CadastroLeadRequestDTO dto) {
        Lead lead = cadastroLeadMapper.toEntity(dto);
        if (!cadastroLeadRepository.existsById(id)) {
            throw new LeadNaoEncontradoException("Lead não consta nos registros");
        }
        lead.setId(id);
        Lead cadastradoNovo = cadastroLeadRepository.save(lead);
        return cadastradoNovo;
    }
}
