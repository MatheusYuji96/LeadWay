# Relatório — ajustes obrigatórios no back-end (`c_backend`)

Ajustes aplicados em 20/11/2026 para alinhar as entidades ao banco definitivo `a_docs/LeadWay-Banco.sql`.

## Status final

- **Compilação: OK.**
  - `.\mvnw.cmd -q -DskipTests compile` → concluído sem erros.
  - `.\mvnw.cmd -q -DskipTests clean compile` → concluído sem erros (executado para remover classes obsoletas de `PreferenciasVendedor` no `target/classes`).
- **Nenhum commit e nenhum push foram realizados.**
- **Nenhum arquivo em `b_frontend` foi alterado.**
- `a_docs/ENDPOINTS_FALTANTES.md` foi preservado.
- Mudanças locais preexistentes foram preservadas:
  - `Lead.java` → `@Table(name = "lead")`
  - `Vendedor.java` → `@Table(name = "usuario")`

## Decisões principais

1. **Compatibilidade de nomes Java/API**
   - Mantive `Lead.vendedor` apontando para a coluna `responsavel_id`.
   - Mantive `Interacao.vendedor` apontando para `usuario_id`.
   - Mantive os nomes externos `vendedorId` em DTOs/controllers, evitando quebrar o contrato com o front.
   - Em `CalConectarRequestDTO`, o campo interno passou a ser `usuarioId`, mas aceita também `vendedorId` via `@JsonAlias`, além de manter getters/setters deprecados de compatibilidade.

2. **Prioridade do Lead**
   - Optei por **`String` + `@Column(columnDefinition = "ENUM('ALTA','MEDIA','BAIXA')")`**, em vez de criar um enum Java.
   - Motivo: menor impacto no contrato e compilação sem mudanças amplas em DTOs.
   - Ainda assim, `prioridade` foi adicionada aos DTOs de cadastro/atualização/response e ao mapper, com validação `@Pattern(regexp = "ALTA|MEDIA|BAIXA")`.

3. **Cargo obrigatório**
   - `cargoId` passou a ser obrigatório no cadastro (`CadastroVendedorRequestDTO` e `VendedorRequestDTO`) e opcional no PATCH (`AtualizarVendedorRequestDTO`).
   - Como `VendedorMapper` é estático, a resolução do `Cargo` foi feita nos services (`VendedorService` e `AuthService`) via `CargoRepository.findById(...)`.
   - Mudança de contrato de entrada: o front precisará enviar `cargoId` nos cadastros de usuário/vendedor.

4. **`schema.sql`**
   - **Não foi alterado**, conforme decidido durante a execução.
   - Continua obsoleto e conflitante (`leads`, `vendedor`, `farol`, `vinculado_em`, `preferencias_vendedor`, enum antigo de contato etc.).
   - Com `spring.jpa.hibernate.ddl-auto=validate` e sem `spring.sql.init.mode` configurado, o Spring Boot não executa esse script automaticamente em MySQL, mas o arquivo ainda precisa de decisão separada (atualização ou remoção).

## Arquivos criados

### Entidades

- `src/main/java/com/leadway/entity/Cargo.java`
  - Nova entidade para `cargo`.
  - `id`, `nome` (`VARCHAR(90)`, `NOT NULL`, `UNIQUE`) e `descricao` (`VARCHAR(145)`).

- `src/main/java/com/leadway/entity/Cadencia.java`
  - Nova entidade para `cadencia`.
  - Relacionamentos: `contato`, `lead` (`NOT NULL`) e `responsavel` (`usuario_id`).
  - Campos: `frequencia`, `intervaloDias`, `dataInicio`, `dataFim`, `status`, `criadoEm`, `atualizadoEm`.
  - Enums mapeados com `@Enumerated(EnumType.STRING)` e `columnDefinition` compatível com o banco.

- `src/main/java/com/leadway/entity/FrequenciaCadencia.java`
  - `DIARIA`, `SEMANAL`, `QUINZENAL`, `MENSAL`, `PERSONALIZADA`.

- `src/main/java/com/leadway/entity/StatusCadencia.java`
  - `ATIVA`, `PAUSADA`, `CONCLUIDA`, `CANCELADA`.

- `src/main/java/com/leadway/entity/Log.java`
  - Nova entidade para `log`: `id`, `tipo` (`VARCHAR(45)`), `mensagem` (`VARCHAR(100)`) e `criadoEm`.

### Repositórios

- `src/main/java/com/leadway/repository/CargoRepository.java`
  - `JpaRepository<Cargo, Integer>` + `findByNome(String nome)`.

- `src/main/java/com/leadway/repository/CadenciaRepository.java`
  - `JpaRepository<Cadencia, Integer>`.

- `src/main/java/com/leadway/repository/LogRepository.java`
  - `JpaRepository<Log, Integer>`.

## Arquivos removidos

- `src/main/java/com/leadway/entity/PreferenciasVendedor.java`
- `src/main/java/com/leadway/repository/PreferenciasVendedorRepository.java`
- `src/main/java/com/leadway/controller/PreferenciasVendedorController.java`

Motivo: não existe tabela `preferencias_vendedor` no banco definitivo.

## Arquivos alterados

### Entidades

#### `entity/Lead.java`

- Preservado `@Table(name = "lead")`.
- `@JoinColumn(name = "vendedor_id")` → **`@JoinColumn(name = "responsavel_id", nullable = false)`**.
- Removidos `vinculadoEm` e `farol`, inclusive getters/setters.
- `prioridade` agora usa `ENUM('ALTA','MEDIA','BAIXA')`.
- `potencial` agora usa `@Column(nullable = false, columnDefinition = "TINYINT UNSIGNED")`.
- Nenhuma renomeação da propriedade `vendedor`, preservando JPQL, services e DTOs.

#### `entity/Vendedor.java`

- Preservado `@Table(name = "usuario")`.
- Adicionado `Cargo cargo` com `@JoinColumn(name = "cargo_id", nullable = false)` + getter/setter.
- `regiao` ajustado de `VARCHAR(100)` para **`VARCHAR(90)`**.

#### `entity/Interacao.java`

- `@JoinColumn(name = "vendedor_id")` → **`@JoinColumn(name = "usuario_id")`**.
- Removido campo `titulo`, getter e setter.
- Propriedade Java `vendedor` mantida para não quebrar queries e services.

#### `entity/Tarefa.java`

- Adicionados:
  - `Vendedor responsavel` → `@JoinColumn(name = "responsavel_id")`.
  - `LocalDateTime concluidoEm`.
  - `LocalDateTime lembreteEm`.
  - Getters/setters correspondentes.
- Ajustado `columnDefinition` do status para `ENUM('ABERTA','CONCLUIDA','CANCELADA')` sem espaços.
- Adicionado `setStatus(...)` para completar o acesso à propriedade já existente.

#### `entity/Contato.java`

- `@JoinColumn(name = "lead_id")` → **`@JoinColumn(name = "lead_id", nullable = false)`**.
- `columnDefinition` da classificação → **`ENUM('PADRAO','SECUNDARIO','PRINCIPAL')`**.

#### `entity/ClassificacaoContato.java`

- Valores alterados de `PADRAO, INFLUENCIADOR, DECISOR` para:
  - **`PADRAO, SECUNDARIO, PRINCIPAL`**.

#### `entity/CalIntegracao.java`

- PK alterada para **`@Id @Column(name = "usuario_id") private Integer usuarioId`**.
- `getVendedorId()/setVendedorId(...)` substituídos por **`getUsuarioId()/setUsuarioId(...)`**.

### Repositórios

#### `repository/InteracaoRepository.java`

- Corrigida query quebrada:
  - `from Interacaoi` → **`from Interacao i`**.
- Mantidas as demais queries com `i.lead.vendedor...`, válidas porque a propriedade Java `vendedor` foi preservada.

#### `repository/LeadEtapaHistoricoRepository.java`

- Corrigida query nativa:
  - `INNER JOIN leads l` → **`INNER JOIN \`lead\` l`**.
  - `l.vendedor_id = :vendedorId` → **`l.responsavel_id = :vendedorId`**.
- Mantido o nome do parâmetro `:vendedorId` para compatibilidade com o service.

### DTOs de request

#### `dto/request/CadastroLeadRequestDTO.java`

- Corrigida validação inválida em `Integer`:
  - `@NotBlank` → **`@NotNull`**.
- Adicionado `@Min(1) @Max(5)` em `potencial`.
- Adicionado campo `prioridade` com `@Pattern(regexp = "ALTA|MEDIA|BAIXA")` + getter/setter.

#### `dto/request/AtualizarLeadRequestDTO.java`

- Adicionado `@Min(1) @Max(5)` em `potencial`.
- Adicionado campo opcional `prioridade` com `@Pattern(...)` + getter/setter.

#### `dto/request/CadastroInteracaoRequestDTO.java`

- Removidos campo `titulo`, anotações e getter/setter.

#### `dto/request/CadastroVendedorRequestDTO.java`

- Adicionado `cargoId` com `@NotNull` + getter/setter.
- Senha ajustada para no máximo 50 caracteres no campo, getter e setter:
  - `@Size(min = 8)` → **`@Size(min = 8, max = 50)`**.

#### `dto/request/VendedorRequestDTO.java`

- Adicionado `cargoId` com `@NotNull` + getter/setter.

#### `dto/request/AtualizarVendedorRequestDTO.java`

- Adicionado `cargoId` opcional + getter/setter para uso no PATCH.

#### `dto/request/CalConectarRequestDTO.java`

- Campo principal renomeado para `usuarioId` + getter/setter.
- `@JsonAlias("vendedorId")` adicionado para aceitar o payload antigo.
- Getters/setters `getVendedorId()/setVendedorId(...)` mantidos como `@Deprecated` para compatibilidade.

#### `dto/request/AtualizarContatoRequestDTO.java`

- Exemplo do Swagger alterado de `DECISOR` para **`PRINCIPAL`**.

### DTOs de response

#### `dto/response/LeadResponseDTO.java`

- Removido `vinculadoEm` e acessadores.
- Adicionado `prioridade` + getter/setter.

#### `dto/response/InteracaoResponseDTO.java`

- Removido `titulo` e acessadores.

#### `dto/response/VendedorResponseDTO.java`

- Adicionados `cargoId` e `cargoNome` + getters/setters.

### Mappers

#### `mapper/CadastroLeadMapper.java`

- Removido `setVinculadoEm(...)`.
- Passou a mapear `prioridade` de request → entity e entity → response.

#### `mapper/InteracaoMapper.java`

- Removido `dto.setTitulo(interacao.getTitulo())`.

#### `mapper/VendedorMapper.java`

- `toResponse(...)` passou a preencher `cargoId` e `cargoNome` quando `vendedor.getCargo()` não é nulo.
- `toEntity(...)` permanece sem resolver `Cargo`, pois a resolução foi feita nos services.

### Services

#### `service/LeadService.java`

- Removidas chamadas a `setVinculadoEm(...)`.
- `salvar(...)` agora exige `vendedorId` porque `responsavel_id` é `NOT NULL`:
  - ausência → `VendedorNaoEncontradoException("Vendedor responsável é obrigatório")`;
  - vendedor inativo → `InativoException`.
- `atualizar(...)` passou a aplicar `prioridade` quando informada.

#### `service/InteracaoService.java`

- Removido `interacao.setTitulo(dto.getTitulo())`.

#### `service/ContatoService.java`

- Corrigida validação quebrada da classificação. Antes havia uma cadeia de `if (!...DECISOR)`, `else if (!...INFLUENCIADOR)` etc., que sempre rejeitava valores válidos.
- Agora o valor é atribuído diretamente quando presente; os valores válidos vêm do próprio enum.

#### `service/VendedorService.java`

- Injetado `CargoRepository`.
- `cadastrar(...)` resolve e seta o cargo via `findById`.
- `atualizar(...)` aplica novo cargo quando `cargoId` é enviado.
- Adicionado helper privado `buscarCargo(Integer)` com tratamento para cargo ausente.

#### `service/AuthService.java`

- Injetado `CargoRepository`.
- Assinatura alterada para:
  - `cadastrar(Vendedor vendedor, String confirmarSenha, Integer cargoId)`.
- Cargo é resolvido e setado antes de `repository.save(...)`.

#### `service/CalIntegracaoService.java`

- Parâmetros internos renomeados para `usuarioId`.
- `integracao.setVendedorId(...)` → **`integracao.setUsuarioId(...)`**.
- Nomes públicos dos métodos mantidos, então `AgendaService` não precisou mudar.

### Controllers

#### `controller/AuthController.java`

- Passa `dto.getCargoId()` para `AuthService.cadastrar(...)`.

#### `controller/CalIntegracaoController.java`

- `conectar(...)` passou a chamar `calIntegracaoService.conectar(dto.getUsuarioId(), ...)`.
- `status` e `desconectar` continuam aceitando `@RequestParam Integer vendedorId`, preservando o contrato externo.

#### `controller/InteracaoController.java`

- Verificado: não havia referência a `titulo`, portanto nenhuma alteração foi necessária.

#### `AgendaService` / `AgendaController`

- Verificados: não havia uso de `CalIntegracao.getVendedorId()`; apenas getters de credenciais, que permaneceram. Nenhuma alteração necessária.

### Configuração

#### `src/main/resources/application.properties`

- `spring.jpa.hibernate.ddl-auto=update` → **`spring.jpa.hibernate.ddl-auto=validate`**.

## Erros de compilação

Nenhum.

Comandos executados em `c_backend`:

1. `.\mvnw.cmd -q -DskipTests compile` — sem saída, exit code 0.
2. `.\mvnw.cmd -q -DskipTests clean compile` — sem saída, exit code 0.
3. `.\mvnw.cmd -q -DskipTests compile` novamente após o clean — sem saída, exit code 0.

Testes não foram executados, conforme `-DskipTests`.

## Observações finais

- O mapeamento continua com nomes Java compatíveis (`vendedor`, `vendedorId`), reduzindo impacto em services, DTOs e front.
- A única mudança de contrato de entrada mais relevante é a exigência de `cargoId` no cadastro de usuário/vendedor, decorrente de `usuario.cargo_id NOT NULL`.
- `CalConectarRequestDTO` foi mantido retrocompatível para aceitar `vendedorId` no JSON.
- `schema.sql` continua desatualizado e conflitante com o banco definitivo; foi deliberadamente **não substituído** nesta tarefa.
