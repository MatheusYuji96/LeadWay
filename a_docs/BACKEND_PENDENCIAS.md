# LeadWay — Pendências do Back-end (checklist de implementação)

Documento de apoio para acrescentar funcionalidades e correções no back-end (`c_backend/`).
Marque os itens conforme forem concluídos.

## Contexto e escopo

- Diagnóstico feito sobre a branch **`testeIntegracao`** (árvore de trabalho limpa).
- **JWT: fora de escopo neste momento.** Sessão continua no front-end (localStorage). Token/perfil de acesso ficam como evolução futura.
- Validações de login (tentativas e bloqueio) **já foram implementadas, mas estão na branch remota `origin/feature/tentativas-login`**, ainda não mesclada aqui. O commit `73f30b4` adiciona:
  - campos `tentativasLogin` e `bloqueadoAte` em `Vendedor`;
  - `ContaBloqueadaException` com HTTP 423;
  - lógica em `AuthService.autenticar`: 3 falhas → bloqueio de 15 min; sucesso zera o contador.
- Este documento **não contém código**, apenas o que deve ser criado/ajustado e o critério para considerar pronto.

## Prioridades

| Prioridade | Significado |
| --- | --- |
| P0 | Desbloqueia o uso correto do que já existe (login + erros) |
| P1 | Endpoints que o front-end já consome e hoje retornam 404 |
| P2 | Funcionalidade inexistente (Tarefas) |
| P3 | Correções de regras, infraestrutura e testes |

## Resumo executivo

| # | Item | Prioridade |
| --- | --- | --- |
| 1 | Mesclar branch de login + migration + handler do 423 + validar `ativo` | P0 |
| 2 | Handler global de exceções e validações | P0 |
| 3 | Endpoints faltantes (notificações, senha, detalhe de vendedor, detalhe de lead, catálogos) | P1 |
| 4 | CRUD de Tarefas | P2 |
| 5 | Correções de regras de negócio | P3 |
| 6 | Configuração de produção (CORS, perfis, secrets, migrations) | P3 |
| 7 | Testes automatizados | P3 |
| 8 | Hash da senha (independente de JWT) | P3 |

---

## P0 — 1. Integrar as validações de login

- [ ] Mesclar `origin/feature/tentativas-login` na branch de integração (`testeIntegracao` / `development`).
- [ ] Criar **migration SQL** para as colunas em `vendedor`:
  - `tentativas_login` (inteiro, obrigatório, padrão `0`);
  - `bloqueado_ate` (datetime, nullable).
  - Atenção: a entidade marca `tentativasLogin` como `nullable = false` sem `DEFAULT` no banco. Com `ddl-auto=update` e linhas já existentes, a criação da coluna pode falhar — o script deve preencher os registros antigos com `0` antes de aplicar a restrição.
- [ ] Incluir `ContaBloqueadaException` no **handler global de erros** (item 2), garantindo corpo `{ "message": "..." }` com status 423. Hoje, sem handler, o corpo é o padrão do Spring e o front não consegue exibir a mensagem.
- [ ] Verificar o campo `ativo` em `AuthService.autenticar` — vendedor inativo não deve conseguir entrar (o fluxo novo de bloqueio não cobre isso).
- [ ] Validar manualmente o ciclo completo: 1ª e 2ª falhas apenas mensagem de credenciais inválidas; 3ª falha → 423; tentativa dentro dos 15 min → 423; após 15 min → contador zerado e login permitido; login correto → contador zerado.

**Critério de aceite:** bloqueio e desbloqueio funcionam via API, com mensagem legível no corpo da resposta e colunas persistidas no banco.

## P0 — 2. Handler global de exceções e validações

Hoje existe apenas `CalExceptionHandler`, limitado a `AgendaController` e `CalIntegracaoController`. Todas as outras exceções caem como 500.

- [ ] Criar um **`@RestControllerAdvice` global** (sem filtro de `assignableTypes`) que devolva `status HTTP + { "message": "texto" }`.
- [ ] Cobrir as exceções customizadas existentes:
  - `CredenciaisInvalidasException`, `SenhasDiferentesException`, `ContaBloqueadaException`;
  - `EmailJaCadastradoException`, `CNPJJaCadastradoException`, `EntidadeConflitoException`;
  - `LeadNaoEncontradoException`, `LeadVazioException`, `VendedorNaoEncontradoException`, `ContatoNaoExistenteException`;
  - `SetorObrigatorioException`, `SetorNaoExistenteException`, `EtapaNaoRegistradaException`, `EnderecoObrigatorioException`;
  - `DataInvalidaException`, `InativoException`, `LeadDiferenteException`, `ClassificacaoNaoValidaException`.
- [ ] Tratar `MethodArgumentNotValidException` (erros de `@Valid`) e `HttpMessageNotReadableException` (JSON inválido) no mesmo padrão.
- [ ] Definir status por tipo: 400 (dado inválido), 401/423 (credenciais/bloqueio), 404 (não encontrado), 409 (conflito), 500 (inerente).
- [ ] Manter o `CalExceptionHandler` apenas para a agenda ou uni-lo ao advice global — desde que agenda e integração continuem devolvendo `{ "message": ... }`.
- [ ] Adicionar `@Valid` onde falta: corpo de `POST /appointments` (`AgendamentoRequestDTO`) e `PUT /integracoes/calcom` (`CalConectarRequestDTO`), com as respectivas anotações de validação nos DTOs.
- [ ] `PreferenciasVendedorController`: trocar `@RequestBody PreferenciasVendedor` por um DTO de request com validação (evita receber campos de entidade diretamente).

**Critério de aceite:** qualquer erro 4xx da API devolve JSON com `message`; o front exibe essa mensagem sem cair no texto genérico "O servidor respondeu com erro (...)".

---

## P1 — 3. Endpoints faltantes (front-end já consome)

Base confirmada nos arquivos de `b_frontend/leadway/src/services/`.

| Método / Rota | Onde é chamado | Situação hoje |
| --- | --- | --- |
| `GET /notifications` | `notifications.service.js` | 404 — não existe entidade/service/controller |
| `PUT /me/password` | `settings.service.js` | 404 — o front já lança erro avisando que a rota não existe |
| `GET /vendedores/{id}` | `representatives.service.js` | 404 — o front contorna filtrando a lista de `/vendedores` |
| `GET /leads/{id}` (agregado) | contrato `docs/API_CONTRACTS.md` | só existe `GET /leads` (lista) |
| `GET /setores`, `GET /etapas` | necessário para selects do cadastro | entidades/repositórios existem, sem controller |
| `POST /leads/{id}/transfer` | `leads.service.js` | desabilitado em modo real; `PATCH /leads/{id}` cobre só o vínculo |
| `GET /auth/me`, `POST /auth/logout` | `auth.service.js` | 404 — **opcional**: `me()` não é usado e falha no `logout()` é ignorada |

### Detalhamento

- [ ] **Notificações:** criar entidade (vínculo com vendedor, ícone, título, descrição, `tone`, `createdAt`, `read`), repositório, DTO de response, service e `GET /notifications` devolvendo `{ items, unread }`. Se as preferências `newLeads`, `appointmentReminders` e `lateCadenceAlerts` devem gerar avisos automaticamente, acrescentar job agendado (`@Scheduled`) para produzi-los.
- [ ] **Troca de senha:** DTO com `currentPassword` e `newPassword`, validação da senha atual com o hash, regras de complexidade iguais às do cadastro e invalidação do contador de tentativas. Resposta `{ "ok": true }`.
- [ ] **Detalhe do vendedor:** `GET /vendedores/{id}` devolvendo os dados do representante (incluindo `codVendedor`, `regiao`, `lider`, `ativo`). O front precisa disso para a tela de detalhe; hoje ele só recebe a lista.
- [ ] **Detalhe do lead:** `GET /leads/{id}` agregando `lead`, `contacts`, `timeline` (interações), `owner`, `funnel` (etapa atual, dias na etapa, data de entrada) e `cadence` (último contato, próxima ação, plano). Evita que o front faça N chamadas por lead na listagem.
- [ ] **Catálogos:** `GET /setores` e `GET /etapas` ( ordenadas por `ordem`) para alimentar os formulários de cadastro/edição de lead.
- [ ] **Transferência de lead:** endpoint próprio com `representativeId` + `reason`, registrando quem transferiu e quando (o `vinculado_em` já existe).
- [ ] **Anotações:** conferir se `POST /leads/{id}/notes` deve ser endpoint próprio ou se o mapeamento atual para `POST /leads/{id}/interacoes` é suficiente (o campo `title` da tela se perde nesse mapeamento).

> Atenção: `docs/API_CONTRACTS.md` cita `GET /dashboard/overview` e `GET /dashboard/team`, mas o front **realmente** chama `/dashboard/indicadores/{id}`, `/atividades-semana/{id}`, `/proximos-compromissos/{id}`, `/leads-atencao/{id}` e `/dashboard/equipe` — esses já existem. O contrato está desatualizado nessa parte; corrigir o documento junto.

**Critério de aceite:** nenhuma chamada dos services do front retorna 404 por endpoint inexistente (exceto os opcionais de sessão).

---

## P2 — 4. CRUD de Tarefas

A entidade `Tarefa`, o enum `StatusTarefa` e o repositório `TarefaRepository` já existem, a tabela `tarefa` está no `schema.sql`, e o `DashboardService` **lê** tarefas — mas não existe controller, service, DTO nem qualquer forma de criar tarefas pela API.

- [ ] Criar DTOs de request (criação e atualização) e de response.
- [ ] Criar `TarefaService` com regras: prazo obrigatório, status inicial `ABERTA`, vínculo obrigatório com lead e contato, validação de pertencimento do contato ao lead.
- [ ] Criar `TarefaController` com endpoints sugeridos:
  - `GET /leads/{leadId}/tarefas` (com filtro de status);
  - `POST /leads/{leadId}/tarefas`;
  - `PATCH /tarefas/{id}` (edição);
  - `PATCH /tarefas/{id}/status` (concluir/cancelar);
  - `DELETE /tarefas/{id}`.
- [ ] Registrar a criação de tarefa quando um compromisso for agendado (ou decidir explicitamente que agenda e tarefas são independentes).
- [ ] Ao concluir/cancelar, atualizar `atualizadoEm` e refletir nos indicadores do dashboard.

**Critério de aceite:** é possível criar uma tarefa via API e vê-la aparecer em "próximos compromissos" do dashboard.

---

## P3 — 5. Correções de regras de negócio

| # | Arquivo / método | Problema | Ação |
| --- | --- | --- | --- |
| 1 | `ContatoService.renovar` | Validação da classificação usa `!equals` encadeado — **nenhuma** opção válida passa | Aceitar explicitamente `PADRAO`, `INFLUENCIADOR` e `DECISOR` |
| 2 | `ContatoService.renovarLead` | Altera o lead mas não persiste (sem `save`/`@Transactional`) | Garantir persistência e validar se o novo lead está ativo |
| 3 | `LeadService.atualizar` | `dataPrimeiraVisita.isBefore(dataPrimeiroContato)` lança NPE se o primeiro contato for nulo | Validar nulo antes de comparar |
| 4 | `LeadService.salvar` | Vendedor inativo é ignorado silenciosamente (lead fica sem responsável) | Retornar erro ou aviso explícito |
| 5 | `DashboardService.buscarIndicadores` | `leadsSemAtualizacao` está fixo em `3` | Calcular a partir da data da última interação/tarefa |
| 6 | `LeadService.atualizar` | `LeadEtapaHistorico` nunca é gravado quando a etapa muda | Registrar cada troca de etapa (o dashboard de atividades da semana depende disso) |
| 7 | `AuthService.autenticar` | Não verifica `ativo` | Ver item P0-1 |
| 8 | `AgendamentoRequestDTO`, `CalConectarRequestDTO` | Sem `@Valid` nos controllers | Ver item P0-2 |
| 9 | `PreferenciasVendedorController` | Recebe entidade no corpo | Ver item P0-2 |

---

## P3 — 6. Configuração / infraestrutura

- [ ] **CORS** no back-end: hoje só o proxy do Vite resolve em desenvolvimento. Em produção, com domínios diferentes, as chamadas falharão.
- [ ] Mover credenciais do MySQL de `application.properties` para variáveis de ambiente.
- [ ] Criar perfis `application-dev.properties` e `application-prod.properties`.
- [ ] Em produção: desligar `spring.jpa.show-sql` e trocar `ddl-auto=update` por validação/migrations.
- [ ] Adotar **Flyway ou Liquibase** para versionar o schema (hoje são `schema.sql` + scripts avulsos em `a_docs/`).
- [ ] **Hash da senha** com `PasswordEncoder` (BCrypt) no cadastro, na troca de senha e na comparação do login — independente da decisão sobre JWT.
- [ ] Criptografar ou armazenar em cofre a `cal_api_key` da tabela `cal_integracao`.
- [ ] Atualizar `README.md` (seções "Funcionalidades" e "Instalação e uso" ainda dizem "A definir").

---

## P3 — 7. Testes automatizados

Hoje existe apenas `LeadwayApplicationTests.contextLoads()`.

- [ ] Testes unitários dos services (regras de `LeadService`, `ContatoService`, `AuthService`, `AgendaService`).
- [ ] Testes de integração dos controllers com MockMvc, cobrindo os fluxos de erro e o formato `{ "message": ... }`.
- [ ] Cenários do login: credenciais erradas, 3ª falha, bloqueio ativo, desbloqueio por tempo, vendedor inativo.
- [ ] Testes do handler global de exceções.
- [ ] Testes do CRUD de Tarefas e do impacto no dashboard.
- [ ] Considerar H2 (perfil de teste) para não depender do MySQL local.

---

## Apêndice A — Inventário de endpoints

### Já existentes

| Área | Endpoints |
| --- | --- |
| Auth | `POST /auth`, `POST /auth/login` |
| Vendedores | `GET /vendedores`, `POST /vendedores`, `PATCH /vendedores/{id}`, `DELETE /vendedores/{id}`, `GET/PUT /vendedores/{id}/preferencias` |
| Leads | `GET /leads`, `POST /leads`, `PATCH /leads/{id}`, `DELETE /leads/{id}` |
| Contatos | `GET/POST /leads/{leadId}/contatos`, `PATCH /leads/{leadId}/contatos/{id}`, `PATCH .../mudar-lead/{novoLeadId}`, `DELETE .../{id}` |
| Interações | `GET/POST /leads/{leadId}/interacoes`, `PUT/DELETE /leads/{leadId}/interacoes/{id}` |
| Dashboard | `GET /dashboard/indicadores/{id}`, `/proximos-compromissos/{id}`, `/atividades-semana/{id}`, `/leads-atencao/{id}`, `/dashboard/equipe` |
| Agenda | `GET /agenda`, `GET /agenda/slots`, `POST /appointments`, `DELETE /appointments/{uid}` |
| Integração Cal.com | `GET/PUT/DELETE /integracoes/calcom` |

### A criar

`GET /vendedores/{id}` · `GET /leads/{id}` · `POST /leads/{id}/transfer` · `GET /setores` · `GET /etapas` · `GET /notifications` · `PUT /me/password` · CRUD de tarefas · (opcional) `GET /auth/me`, `POST /auth/logout`, `GET/PUT /me/profile`, `PUT /me/preferences`, `GET /dashboard/overview`, `GET /dashboard/team`.

## Apêndice B — Arquivos citados

- `c_backend/pom.xml`
- `c_backend/src/main/resources/application.properties`
- `c_backend/src/main/resources/schema.sql`
- `c_backend/src/main/java/com/leadway/service/{AuthService,LeadService,ContatoService,DashboardService}.java`
- `c_backend/src/main/java/com/leadway/controller/{AuthController,VendedorController,LeadController,PreferenciasVendedorController,AgendaController,CalIntegracaoController}.java`
- `c_backend/src/main/java/com/leadway/exception/CalExceptionHandler.java`
- `c_backend/src/main/java/com/leadway/dto/request/{AgendamentoRequestDTO,CalConectarRequestDTO}.java`
- `c_backend/src/main/java/com/leadway/entity/{Vendedor,Tarefa,LeadEtapaHistorico}.java`
- `a_docs/MIGRACAO_*.sql`
- `b_frontend/leadway/docs/API_CONTRACTS.md`
- `b_frontend/leadway/src/services/{auth,leads,representatives,notifications,settings}.service.js`
