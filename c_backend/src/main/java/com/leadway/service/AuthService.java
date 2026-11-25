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
    private final PasswordHashService passwordHash;
    private final com.leadway.repository.CargoRepository cargos;

    public AuthService(VendedorRepository repository, com.leadway.repository.CargoRepository cargos, PasswordHashService passwordHash) {
        this.passwordHash = passwordHash;
        this.repository = repository;
        this.cargos = cargos;
    }

    public Vendedor cadastrar(Vendedor vendedor, String confirmarSenha) {

        // Cadastro público: cada inscrição cria um administrador raiz independente.
        // A organização é identificada pelo próprio administrador (lider == null).
        // O isolamento dos recursos é aplicado pelos endpoints autenticados.
        if (!vendedor.getSenha().equals(confirmarSenha)) {
            throw new SenhasDiferentesException();
        }

        if (repository.existsByEmail(vendedor.getEmail())) {
            throw new EntidadeConflitoException("Ja existe um vendedor com o e-mail " + vendedor.getEmail());
        }

        if (!vendedor.getSenha().matches("^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$")) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Senha exige 8 caracteres, uma maiúscula e um caractere especial");
        vendedor.setSenha(passwordHash.hash(vendedor.getSenha()));
        vendedor.setCargo(cargos.findByNome("ADMINISTRADOR")
            .orElseThrow(() -> new IllegalStateException("Cargo ADMINISTRADOR ausente no banco")));
        vendedor.setLider(null);
        return repository.save(vendedor);
    }

    public Vendedor autenticar(String email, String senha) {

        Vendedor vendedor = repository.findByEmail(email)
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!Boolean.TRUE.equals(vendedor.getAtivo())) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN,"Sua conta está desativada, por favor informe com o supervisor de equipe.");
        java.time.LocalDateTime now=java.time.LocalDateTime.now();
        if (vendedor.getBloqueadoAte()!=null && vendedor.getBloqueadoAte().isAfter(now)) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.LOCKED,"Conta temporariamente bloqueada");
        boolean valid = passwordHash.verify(senha,vendedor.getSenha());
        // Migração gradual de senhas legadas em texto puro, sem modificar a tabela.
        if (!valid && senha != null && senha.equals(vendedor.getSenha())) {
            vendedor.setSenha(passwordHash.hash(senha));
            valid = true;
        }
        if (!valid) {
            int count=(vendedor.getTentativasLogin()==null?0:vendedor.getTentativasLogin())+1;
            vendedor.setTentativasLogin(count>=5?0:count);
            if(count>=5)vendedor.setBloqueadoAte(now.plusMinutes(15));
            repository.save(vendedor);
            throw new CredenciaisInvalidasException();
        }
        vendedor.setTentativasLogin(0);vendedor.setBloqueadoAte(null);repository.save(vendedor);

        return vendedor;
    }
}
