package com.leadway.leadway.service;

import com.leadway.leadway.dto.request.CadastroLeadRequestDTO;
import com.leadway.leadway.entity.Etapa;
import com.leadway.leadway.entity.Lead;
import com.leadway.leadway.entity.Setor;
import com.leadway.leadway.entity.Vendedor;
import com.leadway.leadway.exception.EtapaNaoRegistradaException;
import com.leadway.leadway.exception.SetorNaoExistenteException;
import com.leadway.leadway.exception.VendedorNaoEncontradoException;
import com.leadway.leadway.mapper.CadastroLeadMapper;
import com.leadway.leadway.repository.CadastroLeadRepository;
import com.leadway.leadway.repository.EtapaRepository;
import com.leadway.leadway.repository.SetorRepository;
import com.leadway.leadway.repository.VendedorRepository;

import java.util.Optional;

public class CadastroLeadService {

    private final CadastroLeadRepository cadastroLeadRepository;
    private final EtapaRepository etapaRepository;
    private final SetorRepository setorRepository;
    private final VendedorRepository vendedorRepository;
    private final CadastroLeadMapper cadastroLeadMapper = new CadastroLeadMapper();

    public CadastroLeadService(CadastroLeadRepository cadastroLeadRepository, EtapaRepository etapaRepository, SetorRepository setorRepository, VendedorRepository vendedorRepository) {
        this.cadastroLeadRepository = cadastroLeadRepository;
        this.etapaRepository = etapaRepository;
        this.setorRepository = setorRepository;
        this.vendedorRepository = vendedorRepository;
    }

    public Lead salvar(CadastroLeadRequestDTO dto) {
        Lead cadastro = cadastroLeadMapper.toEntity(dto);
        Optional<Vendedor> vendedorOpt = vendedorRepository.findById(dto.getVendedorId());
        if (vendedorOpt.isEmpty()) {
            throw new VendedorNaoEncontradoException("Vendedor não encontrado");
        }
        Vendedor vendedor = vendedorOpt.get();
        cadastro.setVendedor(vendedor);
        Optional<Setor> setorOpt = setorRepository.findById(dto.getSetorId());
        if (setorOpt.isEmpty()) {
            throw new SetorNaoExistenteException("Setor não encontrado nos registros");
        }
        Setor setor = setorOpt.get();
        cadastro.setSetor(setor);
        Etapa etapa;
        if (dto.getEtapaId() == null) {
            etapa = etapaRepository.findFirstByIdAsc();
            cadastro.setEtapa(etapa);
        } else {
            Optional<Etapa> etapaOpt = etapaRepository.findById(dto.getEtapaId());
            if(etapaOpt.isEmpty()){
                throw new EtapaNaoRegistradaException("Etapa não registrada");
            }
            etapa = etapaOpt.get();
            cadastro.setEtapa(etapa);
        }
        Lead leadCadastrar = cadastroLeadRepository.save(cadastro);
        return leadCadastrar;
    }
}
