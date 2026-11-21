package com.leadway.service;

import com.leadway.entity.Cargo;
import com.leadway.entity.Vendedor;
import com.leadway.exception.ContaBloqueadaException;
import com.leadway.exception.CredenciaisInvalidasException;
import com.leadway.exception.EntidadeConflitoException;
import com.leadway.exception.EntidadeNaoEncontradaException;
import com.leadway.exception.SenhasDiferentesException;
import com.leadway.repository.CargoRepository;
import com.leadway.repository.VendedorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final VendedorRepository repository;
    private final CargoRepository cargoRepository;
    private static final Integer MAX_TENTATIVAS = 3;
    private static final Integer TEMPO_BLOQUEIO = 15;

    public AuthService(VendedorRepository repository, CargoRepository cargoRepository) {
        this.repository = repository;
        this.cargoRepository = cargoRepository;
    }

    public Vendedor cadastrar(Vendedor vendedor, String confirmarSenha, Integer cargoId) {

        if (cargoId == null) {
            throw new EntidadeNaoEncontradaException("Cargo é obrigatório");
        }
        Cargo cargo = cargoRepository.findById(cargoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cargo não encontrado"));
        vendedor.setCargo(cargo);

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

        LocalDateTime dataHoraAtual = LocalDateTime.now();

        if (vendedor.getBloqueadoAte() != null) {
            if (dataHoraAtual.isBefore(vendedor.getBloqueadoAte())) {
                throw new ContaBloqueadaException();
            }

            vendedor.setBloqueadoAte(null);
            vendedor.setTentativasLogin(0);
        }

        if (!vendedor.getSenha().equals(senha)) {

            Integer tentativas = vendedor.getTentativasLogin() + 1;

            vendedor.setTentativasLogin(tentativas);

            if (tentativas >= MAX_TENTATIVAS) {
                vendedor.setBloqueadoAte(dataHoraAtual.plusMinutes(TEMPO_BLOQUEIO));
            }

            repository.save(vendedor);

            throw new CredenciaisInvalidasException();
        }

        vendedor.setBloqueadoAte(null);
        vendedor.setTentativasLogin(0);

        repository.save(vendedor);

        return vendedor;
    }
}
