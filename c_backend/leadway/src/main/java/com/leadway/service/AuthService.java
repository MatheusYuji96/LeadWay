package com.leadway.service;

import com.leadway.entity.Vendedor;
import com.leadway.exception.CredenciaisInvalidasException;
import com.leadway.exception.EntidadeConflitoException;
import com.leadway.exception.SenhasDiferentesException;
import com.leadway.repository.VendedorRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final VendedorRepository repository;

    public AuthService(VendedorRepository repository) {
        this.repository = repository;
    }

    public Vendedor cadastrar(Vendedor vendedor, String confirmarSenha) {

        if (!vendedor.getSenha().equals(confirmarSenha)) {
            throw new SenhasDiferentesException();
        }

        if (repository.existsByEmail(vendedor.getEmail())) {
            throw new EntidadeConflitoException("Ja existe um vendedor com o e-mail " + vendedor.getEmail());
        }

        return repository.save(vendedor);
    }

    public Vendedor autenticar(String email, String senha) {

        Vendedor vendedor = repository.findByEmail(email)
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!vendedor.getSenha().equals(senha)) {
            throw new CredenciaisInvalidasException();
        }

        return vendedor;
    }
}
