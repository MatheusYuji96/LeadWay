package com.leadway.service;

import com.leadway.dto.request.AtualizarContatoRequestDTO;
import com.leadway.dto.request.ContatoRequestDTO;
import com.leadway.entity.Contato;
import com.leadway.entity.Lead;
import com.leadway.exception.ContatoNaoExistenteException;
import com.leadway.exception.EmailJaCadastradoException;
import com.leadway.exception.LeadNaoEncontradoException;
import com.leadway.mapper.ContatoMapper;
import com.leadway.repository.LeadRepository;
import com.leadway.repository.ContatoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContatoService {

    private final ContatoRepository contatoRepository;
    private final LeadRepository leadRepository;
    private final ContatoMapper contatoMapper = new ContatoMapper();

    public ContatoService(ContatoRepository contatoRepository, LeadRepository leadRepository) {
        this.contatoRepository = contatoRepository;
        this.leadRepository = leadRepository;
    }

    public List<Contato> listarPorFiltro(String termo, Integer leadId){
        List<Contato> lista = contatoRepository.findByFiltros(termo, leadId);
        return lista;
    }

    public Contato salvar(ContatoRequestDTO dto){
        Contato contato = contatoMapper.toEntity(dto);
        if (contatoRepository.existsByEmailIgnoreCase(dto.getEmail())){
            throw new EmailJaCadastradoException("Email já cadastrado anteriormente");
        }
        if (dto.getLeadId() != null) {
            Optional<Lead> leadOpt = leadRepository.findById(dto.getLeadId());
            if (leadOpt.isEmpty()) {
                throw new LeadNaoEncontradoException("Lead informado não existe");
            }
            Lead lead = leadOpt.get();
            contato.setLead(lead);
        }
        contatoRepository.save(contato);
        return contato;
    }

    public Contato renovar(Integer id, AtualizarContatoRequestDTO dto){
        Optional<Contato> contatoOpt = contatoRepository.findById(id);
        if (contatoOpt.isEmpty()){
            throw new ContatoNaoExistenteException("Contato não consta nos registros");
        }
        Contato contato = contatoOpt.get();
        if (dto.getCargo() != null){
            contato.setCargo(dto.getCargo());
        }
        if (dto.getEmail() != null){
            contato.setEmail(dto.getEmail());
        }
        if (dto.getWhatsapp() != null){
            contato.setWhatsapp(dto.getWhatsapp());
        }
        if (dto.getAtivo() != null){
            contato.setAtivo(dto.getAtivo());
        }
        if (dto.getContato() != null){
            contato.setClassificacao(dto.getContato());
        }
        Contato contatoRegistrar = contatoRepository.save(contato);
        return contatoRegistrar;
    }

    public Contato renovarLead(Integer contatoId, Integer novoLeadId){
        Optional<Contato> contatoOpt = contatoRepository.findById(contatoId);
        if (contatoOpt.isEmpty()){
            throw new ContatoNaoExistenteException("Contato não consta nos registros");
        }
        Contato contato = contatoOpt.get();
        Optional<Lead> leadOpt = leadRepository.findById(novoLeadId);
        if (leadOpt.isEmpty()){
            throw new LeadNaoEncontradoException("Lead não consta nos registros");
        }
        Lead lead = leadOpt.get();
        contato.setLead(lead);
        return contato;
    }

    public void apagar(Integer id){
        if (!contatoRepository.existsById(id)){
            throw new ContatoNaoExistenteException("Contato não consta nos registros");
        }
        contatoRepository.deleteById(id);
    }
}
