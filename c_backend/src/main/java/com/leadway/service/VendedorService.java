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

    public VendedorService(VendedorRepository vendedorRepository) {
        this.vendedorRepository = vendedorRepository;
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
        vendedorRepository.save(vendedor);
        return vendedor;
    }

    public Vendedor atualizar(Integer id, AtualizarVendedorRequestDTO dto){
        Optional<Vendedor> vendedorOpt = vendedorRepository.findById(id);
        if (vendedorOpt.isEmpty()){
            throw new VendedorNaoEncontradoException("Vendedor não consta nos registros");
        }
        Vendedor vendedor = vendedorOpt.get();
        vendedor.setId(id);
        if (dto.getEmail() != null){
            if (vendedorRepository.existsByEmailIgnoreCase(dto.getEmail())){
                throw new EmailJaCadastradoException("Email já cadastrado anteriormente");
            }
            vendedor.setEmail(dto.getEmail());
        }
        if (dto.getTelefone() != null){
            vendedor.setTelefone(dto.getTelefone());
        }
        if (dto.getSenha() != null){
            vendedor.setSenha(dto.getSenha());
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
            vendedor.setLider(lider);
        }
        Vendedor vendedorLocalizado = vendedorRepository.save(vendedor);
        return vendedorLocalizado;
    }

    public void apagar(Integer id){
        if (!vendedorRepository.existsById(id)){
            throw new VendedorNaoEncontradoException("Vendedor não consta nos registros");
        }
        vendedorRepository.deleteById(id);
    }
}
