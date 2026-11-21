package com.leadway.service;

import com.leadway.entity.Vendedor;
import com.leadway.repository.VendedorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccountService {
    private final VendedorRepository users;
    private final PasswordHashService passwords;

    public AccountService(VendedorRepository users, PasswordHashService passwords) {
        this.users = users;
        this.passwords = passwords;
    }

    @Transactional
    public Vendedor update(Vendedor actor, String nome, String email, String telefone) {
        if (nome == null || nome.isBlank() || nome.length() > 190)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome inválido");
        if (email == null || email.length() > 255 || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail inválido");
        if (telefone == null || !telefone.matches("\\d{11}"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefone deve conter 11 dígitos");
        if (users.existsByEmailIgnoreCase(email) && !actor.getEmail().equalsIgnoreCase(email))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        actor.setNome(nome.trim());
        actor.setEmail(email.trim());
        actor.setTelefone(telefone);
        return users.save(actor);
    }

    @Transactional
    public void changePassword(Vendedor actor, String current, String next) {
        if (current == null || !passwords.verify(current, actor.getSenha()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha atual incorreta");
        if (next == null || !next.matches("^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{8,}$"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A nova senha exige 8 caracteres, uma maiúscula e um caractere especial");
        if (current.equals(next))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A nova senha deve ser diferente da atual");
        actor.setSenha(passwords.hash(next));
        users.save(actor);
    }
}
