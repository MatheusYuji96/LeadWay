package com.leadway.service;

import com.leadway.dto.request.VendedorRequestDTO;
import com.leadway.entity.Vendedor;
import com.leadway.exception.EmailJaCadastradoException;
import com.leadway.exception.VendedorNaoEncontradoException;
import com.leadway.mapper.VendedorMapper;
import com.leadway.repository.VendedorRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VendedorService {

    private final VendedorMapper vendedorMapper = new VendedorMapper();
    private final VendedorRepository vendedorRepository;

    public VendedorService(VendedorRepository vendedorRepository) {
        this.vendedorRepository = vendedorRepository;
    }

    public Vendedor cadastrar(VendedorRequestDTO dto){
        Vendedor vendedor = vendedorMapper.toEntity(dto);
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
