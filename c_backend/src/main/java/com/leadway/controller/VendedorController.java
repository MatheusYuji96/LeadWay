package com.leadway.controller;

import com.leadway.dto.request.AtualizarSenhaRequestDTO;
import com.leadway.dto.request.AtualizarVendedorRequestDTO;
import com.leadway.dto.request.VendedorRequestDTO;
import com.leadway.dto.response.VendedorDetalheResponseDTO;
import com.leadway.dto.response.VendedorResponseDTO;
import com.leadway.entity.Vendedor;
import com.leadway.mapper.VendedorMapper;
import com.leadway.service.SessionService;
import com.leadway.service.VendedorDetalheService;
import com.leadway.service.VendedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Tag(
        name = "Vendedores",
        description = "Operações relacionadas ao gerenciamento de vendedores"
)
@RestController
@RequestMapping("/vendedores")
public class VendedorController {

    private final VendedorService vendedorService;
    private final VendedorDetalheService vendedorDetalheService;
    private final SessionService sessions;

    public VendedorController(VendedorService vendedorService,
                              VendedorDetalheService vendedorDetalheService,
                              SessionService sessions) {
        this.vendedorService = vendedorService;
        this.vendedorDetalheService = vendedorDetalheService;
        this.sessions = sessions;
    }

    @Operation(summary = "Lista vendedores cadastrados")
    @GetMapping
    public ResponseEntity<List<VendedorResponseDTO>> listar(@RequestParam(required = false) String termo,
                                                            @RequestHeader(value = "Authorization", required = false) String token) {
        Vendedor actor = sessions.require(token);
        sessions.requireManager(actor);
        List<Vendedor> lista = vendedorService.listarPorFiltro(termo).stream()
                .filter(v -> sessions.root(v).getId().equals(sessions.root(actor).getId()))
                .filter(v -> !sessions.role(actor).equals("GERENTE") || sessions.role(v).equals("VENDEDOR"))
                .toList();
        return ResponseEntity.ok(lista.stream().map(VendedorMapper::toResponse).toList());
    }

    @Operation(
            summary = "Detalha um vendedor",
            description = "feature/calcom: indicadores, evolução e carteira, agora protegidos por RBAC."
    )
    @GetMapping("/{id}")
    public ResponseEntity<VendedorDetalheResponseDTO> listarPorId(@PathVariable Integer id,
                                                                  @RequestHeader(value = "Authorization", required = false) String token) {
        Vendedor actor = sessions.require(token);
        Vendedor target = vendedorService.buscar(id);
        sessions.sameOrg(actor, target);
        boolean self = actor.getId().equals(id);
        if (sessions.role(actor).equals("VENDEDOR") && !self)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sem permissão para detalhar outro vendedor");
        if (sessions.role(actor).equals("GERENTE") && !sessions.role(target).equals("VENDEDOR"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Gerente só detalha vendedores");
        if (sessions.role(actor).equals("GERENTE") && !self
                && (target.getLider() == null || !actor.getId().equals(target.getLider().getId())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário fora da equipe");
        return ResponseEntity.ok(vendedorDetalheService.buscar(id));
    }

    @Operation(summary = "Cadastra um novo vendedor")
    @PostMapping
    public ResponseEntity<VendedorResponseDTO> cadastrar(@Valid @RequestBody VendedorRequestDTO requestDTO,
                                                         @RequestHeader(value = "Authorization", required = false) String token) {
        Vendedor actor = sessions.require(token);
        sessions.requireManager(actor);
        if (sessions.role(actor).equals("GERENTE") && "GERENTE".equals(requestDTO.getCargo()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Gerente não cadastra gerente");
        requestDTO.setLiderId(actor.getId());
        Vendedor vendedor = vendedorService.cadastrar(requestDTO);
        return ResponseEntity.status(201).body(VendedorMapper.toResponse(vendedor));
    }

    /**
     * feature/calcom: troca de senha preservada, mas usando PasswordHashService
     * (feature/teste-all) em vez da comparação de texto puro da branch antiga.
     */
    @Operation(summary = "Altera a própria senha")
    @PutMapping("/{id}/senha")
    public ResponseEntity<Void> atualizarSenha(@PathVariable Integer id,
                                               @Valid @RequestBody AtualizarSenhaRequestDTO requestDTO,
                                               @RequestHeader(value = "Authorization", required = false) String token) {
        Vendedor actor = sessions.require(token);
        if (!actor.getId().equals(id))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário só altera a própria senha");
        vendedorService.atualizarPasswd(requestDTO, id);
        return ResponseEntity.status(204).build();
    }

    @Operation(summary = "Atualiza um vendedor")
    @PatchMapping("/{id}")
    public ResponseEntity<VendedorResponseDTO> atualizar(@PathVariable Integer id,
                                                         @Valid @RequestBody AtualizarVendedorRequestDTO requestDTO,
                                                         @RequestHeader(value = "Authorization", required = false) String token) {
        Vendedor actor = sessions.require(token);
        sessions.requireManager(actor);
        Vendedor target = vendedorService.buscar(id);
        sessions.sameOrg(actor, target);
        if (sessions.role(actor).equals("GERENTE") && !sessions.role(target).equals("VENDEDOR"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Gerente só edita vendedores");
        if (requestDTO.getCargo() != null && sessions.role(actor).equals("GERENTE")
                && !"VENDEDOR".equals(requestDTO.getCargo()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Gerente não pode promover a gerente ou administrador");
        if (sessions.role(actor).equals("GERENTE")
                && (target.getLider() == null || !actor.getId().equals(target.getLider().getId())))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Funcionário fora da equipe");
        requestDTO.setLiderId(null); // Alteração de hierarquia requer fluxo separado.
        Vendedor vendedor = vendedorService.atualizar(id, requestDTO);
        return ResponseEntity.ok(VendedorMapper.toResponse(vendedor));
    }

    @Operation(summary = "Deleta um vendedor")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id,
                                        @RequestHeader(value = "Authorization", required = false) String token) {
        Vendedor actor = sessions.require(token);
        sessions.requireManager(actor);
        Vendedor target = vendedorService.buscar(id);
        sessions.sameOrg(actor, target);
        if (actor.getId().equals(id) || sessions.role(target).equals("ADMINISTRADOR"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não pode excluir essa conta");
        if (sessions.role(actor).equals("GERENTE") && !sessions.role(target).equals("VENDEDOR"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Gerente só exclui vendedores");
        vendedorService.apagar(id);
        return ResponseEntity.status(204).build();
    }
}
