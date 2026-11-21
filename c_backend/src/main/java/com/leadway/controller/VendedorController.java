package com.leadway.controller;

import com.leadway.dto.request.AtualizarVendedorRequestDTO;
import com.leadway.dto.request.VendedorRequestDTO;
import com.leadway.dto.response.VendedorResponseDTO;
import com.leadway.entity.Vendedor;
import com.leadway.mapper.VendedorMapper;
import com.leadway.service.VendedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Vendedores",
        description = "Operações relacionadas ao gerenciamento de vendedores"
)
@RestController
@RequestMapping("/vendedores")
public class VendedorController {

    private final VendedorService vendedorService;
    private final com.leadway.service.SessionService sessions;

    public VendedorController(VendedorService vendedorService, com.leadway.service.SessionService sessions) {
        this.vendedorService = vendedorService;
        this.sessions = sessions;
    }

    @Operation(
            summary = "Lista vendedores cadastrados",
            description = "Lista representantes comerciais no sistema, com ou sem filtros."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vendedores listados com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    public ResponseEntity<List<VendedorResponseDTO>> listar(@RequestParam(required = false) String termo, @org.springframework.web.bind.annotation.RequestHeader(value="Authorization",required=false) String token) {
        Vendedor actor=sessions.require(token);sessions.requireManager(actor);
        List<Vendedor> lista = vendedorService.listarPorFiltro(termo).stream()
            .filter(v->sessions.root(v).getId().equals(sessions.root(actor).getId()))
            .filter(v->!sessions.role(actor).equals("GERENTE") || sessions.role(v).equals("VENDEDOR"))
            .toList();
        List<VendedorResponseDTO> listaResponse = lista.stream().map(VendedorMapper::toResponse).toList();
        return ResponseEntity.status(200).body(listaResponse);
    }

    @Operation(
            summary = "Cadastra um novo vendedor",
            description = "Cria um novo representante comercial, podendo ser designado à acompanhar um lead específico."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vendedor cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de cadastro inválidos, ausentes ou em formato incorreto"),
            @ApiResponse(responseCode = "404", description = "Vendedor líder não consta no sistema"),
            @ApiResponse(responseCode = "409", description = "E-mail de vendedor já cadastrado no sistema"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping
    public ResponseEntity<VendedorResponseDTO> cadastrar(@Valid @RequestBody VendedorRequestDTO requestDTO,
          @org.springframework.web.bind.annotation.RequestHeader(value="Authorization",required=false) String token) {
        Vendedor actor=sessions.require(token);sessions.requireManager(actor);
        if (sessions.role(actor).equals("GERENTE") && "GERENTE".equals(requestDTO.getCargo()))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN,"Gerente não cadastra gerente");
        requestDTO.setLiderId(actor.getId());
        Vendedor vendedor = vendedorService.cadastrar(requestDTO);
        VendedorResponseDTO responseDTO = VendedorMapper.toResponse(vendedor);
        return ResponseEntity.status(201).body(responseDTO);
    }

    @Operation(
            summary = "Atualiza um vendedor",
            description = "Atualiza uma ou mais informações de um vendedor no sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vendedor atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Vendedor não encontrado no sistema"),
            @ApiResponse(responseCode = "409", description = "E-mail de vendedor já cadastrado no sistema"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<VendedorResponseDTO> atualizar(@PathVariable Integer id, @Valid @RequestBody AtualizarVendedorRequestDTO requestDTO,
          @org.springframework.web.bind.annotation.RequestHeader(value="Authorization",required=false) String token) {
        Vendedor actor=sessions.require(token);sessions.requireManager(actor);
        Vendedor target=vendedorService.buscar(id);sessions.sameOrg(actor,target);
        if(sessions.role(actor).equals("GERENTE") && !sessions.role(target).equals("VENDEDOR"))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN,"Gerente só edita vendedores");
        if (requestDTO.getCargo() != null && sessions.role(actor).equals("GERENTE")
            && !"VENDEDOR".equals(requestDTO.getCargo()))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN,"Gerente não pode promover a gerente ou administrador");
        if (sessions.role(actor).equals("GERENTE") &&
            (target.getLider() == null || !actor.getId().equals(target.getLider().getId())))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN,"Funcionário fora da equipe");
        requestDTO.setLiderId(null); // Alteração de hierarquia requer fluxo separado.
        Vendedor vendedor = vendedorService.atualizar(id, requestDTO);
        VendedorResponseDTO responseDTO = VendedorMapper.toResponse(vendedor);
        return ResponseEntity.status(200).body(responseDTO);
    }

    @Operation(
            summary = "Deleta um vendedor",
            description = "Apaga um vendedor do sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vendedor excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Vendedor não encontrado no sistema"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id,
          @org.springframework.web.bind.annotation.RequestHeader(value="Authorization",required=false) String token) {
        Vendedor actor=sessions.require(token);sessions.requireManager(actor);
        Vendedor target=vendedorService.buscar(id);sessions.sameOrg(actor,target);
        if(actor.getId().equals(id) || sessions.role(target).equals("ADMINISTRADOR"))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN,"Não pode excluir essa conta");
        if(sessions.role(actor).equals("GERENTE") && !sessions.role(target).equals("VENDEDOR"))
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN,"Gerente só exclui vendedores");
        vendedorService.apagar(id);
        return ResponseEntity.status(204).build();
    }
}
