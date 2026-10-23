package com.leadway.service;

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

    private final VendedorMapper vendedorMapper = new VendedorMapper();
    private final VendedorRepository vendedorRepository;

    public VendedorService(VendedorRepository vendedorRepository) {
        this.vendedorRepository = vendedorRepository;
    }

    public List<Vendedor> listarVendedor(){
        List<Vendedor> lista = vendedorRepository.findByFiltros();
        return lista;
    }

    public List<Vendedor> listarPorFiltro(String termo){
        List<Vendedor> lista = vendedorRepository.findByFiltros(termo);
        return lista;
    }

    public Vendedor cadastrar(VendedorRequestDTO dto){
        Vendedor vendedor = vendedorMapper.toEntityVendedor(dto);
        if (vendedorRepository.existsByEmailIgnoreCase(dto.getEmail())){
            throw new EmailJaCadastradoException("Email já cadastrado anteriormente");
        }
        Optional<Vendedor> vendedorOpt = vendedorRepository.findById(dto.getLiderId());
        if (vendedorOpt.isEmpty()){
            throw new VendedorNaoEncontradoException("Vendedor não consta nos registros");
        }
        Vendedor registrado = vendedorOpt.get();
        vendedor.setLider(registrado);
        vendedorRepository.save(vendedor);
        return vendedor;
    }
}
