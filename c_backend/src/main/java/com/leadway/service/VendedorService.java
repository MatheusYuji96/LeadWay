package com.leadway.service;

import com.leadway.dto.request.AtualizarLeadRequestDTO;
import com.leadway.dto.request.AtualizarVendedorRequestDTO;
import com.leadway.dto.request.VendedorRequestDTO;
import com.leadway.entity.Vendedor;
import com.leadway.exception.EmailJaCadastradoException;
import com.leadway.exception.VendedorNaoEncontradoException;
import com.leadway.mapper.VendedorMapper;
import com.leadway.repository.VendedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VendedorService {

    private final VendedorRepository vendedorRepository;
    private final PasswordHashService passwordHash;
    private final com.leadway.repository.CargoRepository cargos;
    private final com.leadway.repository.LeadRepository leads;

    public VendedorService(VendedorRepository vendedorRepository,
                           com.leadway.repository.CargoRepository cargos,
                           com.leadway.repository.LeadRepository leads, PasswordHashService passwordHash) {
        this.passwordHash = passwordHash;
        this.vendedorRepository = vendedorRepository;
        this.cargos = cargos;
        this.leads = leads;
    }

    public List<Vendedor> listarPorFiltro(String termo){
        List<Vendedor> lista = vendedorRepository.findByFiltros(termo);
        return lista;
    }

    public Vendedor cadastrar(VendedorRequestDTO dto){
        Vendedor vendedor = VendedorMapper.toEntityVendedor(dto);
        if (vendedorRepository.existsByEmailIgnoreCase(dto.getEmail())){
            throw new EmailJaCadastradoException("Email já cadastrado anteriormente");
        }
        if (dto.getLiderId() != null){
        Optional<Vendedor> vendedorOpt = vendedorRepository.findById(dto.getLiderId());
        if (vendedorOpt.isEmpty()){
            throw new VendedorNaoEncontradoException("Vendedor líder não consta nos registros");
        }
        Vendedor liderRegistrado = vendedorOpt.get();
        vendedor.setLider(liderRegistrado);
        }
        String cargoNome = dto.getCargo() == null ? "VENDEDOR" : dto.getCargo();
        vendedor.setCargo(cargos.findByNome(cargoNome).orElseThrow(() ->
            new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Cargo inválido")));
        if (vendedor.getSenha()==null || !vendedor.getSenha().matches("^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$")) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Senha inválida");
        vendedor.setSenha(passwordHash.hash(vendedor.getSenha()));
        vendedorRepository.save(vendedor);
        return vendedor;
    }

    public Vendedor buscar(Integer id){return vendedorRepository.findById(id).orElseThrow(()->new VendedorNaoEncontradoException("Funcionário não encontrado"));}

    public Vendedor atualizar(Integer id, AtualizarVendedorRequestDTO dto){
        Optional<Vendedor> vendedorOpt = vendedorRepository.findById(id);
        if (vendedorOpt.isEmpty()){
            throw new VendedorNaoEncontradoException("Vendedor não consta nos registros");
        }
        Vendedor vendedor = vendedorOpt.get();
        vendedor.setId(id);
        if (dto.getCargo() != null) vendedor.setCargo(cargos.findByNome(dto.getCargo()).orElseThrow(() ->
            new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Cargo inexistente")));
        if (dto.getNome() != null) vendedor.setNome(dto.getNome());
        if (dto.getCodVendedor() != null) vendedor.setCodVendedor(dto.getCodVendedor());
        if (dto.getRegiao() != null) vendedor.setRegiao(dto.getRegiao());
        if (dto.getEmail() != null){
            if (!vendedor.getEmail().equalsIgnoreCase(dto.getEmail())) {
                if (vendedorRepository.existsByEmailIgnoreCase(dto.getEmail())) {
                    throw new EmailJaCadastradoException("Email já cadastrado anteriormente");
                }
                vendedor.setEmail(dto.getEmail());
            }
        }
        if (dto.getTelefone() != null){
            vendedor.setTelefone(dto.getTelefone());
        }
        if (dto.getSenha() != null){
            vendedor.setSenha(passwordHash.hash(dto.getSenha()));
        }
        if (dto.getAtivo() != null){
            vendedor.setAtivo(dto.getAtivo());
        }
        if (dto.getLiderId() != null){
            Optional<Vendedor> liderOpt = vendedorRepository.findById(dto.getLiderId());
            if (liderOpt.isEmpty()){
                throw new VendedorNaoEncontradoException("Vendedor não consta nos registros");
            }
            Vendedor lider = liderOpt.get();
            if (lider.getId().equals(id)) throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, "Um usuário não pode ser seu próprio líder");
            vendedor.setLider(lider);
        }
        Vendedor vendedorLocalizado = vendedorRepository.save(vendedor);
        return vendedorLocalizado;
    }

    public void apagar(Integer id){
        if (!vendedorRepository.existsById(id)){
            throw new VendedorNaoEncontradoException("Vendedor não consta nos registros");
        }
        if (leads.existsByVendedor_Id(id)) throw new org.springframework.web.server.ResponseStatusException(
            org.springframework.http.HttpStatus.CONFLICT, "Funcionário possui Leads vinculados");
        if (vendedorRepository.existsByLider_Id(id)) throw new org.springframework.web.server.ResponseStatusException(
            org.springframework.http.HttpStatus.CONFLICT, "Transfira os subordinados antes de excluir");
        vendedorRepository.deleteById(id);
    }
}
