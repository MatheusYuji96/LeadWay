# Contratos de API — LeadWay (frontend)

Este documento descreve **exatamente** os endpoints que o frontend consome, com
request/response esperados e as pendências de definição com o backend.
Nenhum endpoint além destes é chamado pela aplicação.

Base URL: definida em `VITE_API_BASE_URL` (ex.: `https://api.leadway.com`).
Todas as respostas são JSON e os serviços extraem `response.data` com `unwrap()`.

## Modo demonstração (mock)

Enquanto o backend não está disponível, o app roda em **modo demonstração**:

- `VITE_USE_MOCK=true` **ou** `VITE_API_BASE_URL` ausente → adaptador Axios local
  (`src/services/mock/createMockAdapter.js`) atende as rotas abaixo com fixtures
  derivadas do protótipo (`src/services/mock/fixtures.js`).
- `VITE_USE_MOCK=false` + `VITE_API_BASE_URL` definida → chamadas HTTP reais.
- No modo mock, um banner de demonstração é exibido no topo do layout autenticado.
- Os dados exibidos em modo mock **não** são dados reais de backend.

## Pendências em aberto com o backend

1. **Autenticação**: não há definição de token/cookie. A sessão vive apenas em
   memória (`SessionProvider`). Enquanto o mecanismo não for definido:
   - `POST /auth/login` e `POST /auth/signup` retornam o usuário;
   - não há header de autorização sendo enviado;
   - não há guarda de rotas por sessão.
2. **Criação local**: criações com otimismo (novo contato na tela de contatos,
   novo representante na tela de equipe) são mantidas em estado local e devem
   ser substituídas por refetch quando os endpoints de criação existirem.
3. `POST /leads`, `PUT /leads/:id`, `POST /representatives` e
   `POST /appointments` ainda não têm contrato de resposta confirmado; o
   frontend apenas espera sucesso (o mock devolve `{ id }` / `{ ok: true }`).

## Endpoints

### Autenticação e sessão

| Método | Rota | Uso |
| --- | --- | --- |
| POST | `/auth/login` | Tela Entrar |
| POST | `/auth/signup` | Tela Criar conta |

```jsonc
// POST /auth/login  → 200
// request
{ "email": "ana@empresa.com", "password": "********" }
// response
{ "user": { "id": "u1", "name": "Ana Souza", "email": "ana@empresa.com",
            "role": "Coordenadora Comercial", "initials": "AS" } }
```

`POST /auth/signup` aceita `{ name, whatsapp, email, password }` e devolve o
mesmo formato `{ user }`.

### Dashboard

| Método | Rota | Uso |
| --- | --- | --- |
| GET | `/dashboard/overview` | Aba "Minha visão" |
| GET | `/dashboard/team` | Aba "Acompanhamento da equipe" |

```jsonc
// GET /dashboard/overview → 200
{
  "stats": [ { "key", "icon", "label", "value", "detail", "delta", "tone" } ],
  "appointments": [ { "id", "time", "type", "companyName", "contactName", "tone" } ],
  "weekActivity": [ { "day", "contacts", "meetings", "followUps" } ],
  "leadsNeedingAttention": [ Lead ] // ver modelo em src/models/lead.js
}

// GET /dashboard/team → 200
{
  "stats": [ Stat ],
  "performance": [ { "name", "contacts", "meetings", "visits" } ],
  "staleLeads": [ { "id", "name", "representativeName", "stage", "days" } ],
  "representatives": [ Representative ] // src/models/representative.js
}
```

### Leads

| Método | Rota | Uso |
| --- | --- | --- |
| GET | `/leads` | Listagem (params: `search`, `stage`, `cadence`) |
| GET | `/leads/:id` | Detalhe |
| POST | `/leads` | Wizard "Nova empresa" |
| PUT | `/leads/:id` | Wizard "Editar empresa" |
| POST | `/leads/:id/transfer` | Modal "Alterar responsável" |
| POST | `/leads/:id/notes` | Modal "Nova anotação" |

```jsonc
// GET /leads → 200
{ "items": [ Lead ], "total": 128, "stats": { "total", "prospecting", "followUp", "needAttention" } }

// GET /leads/:id → 200
{
  "lead": Lead,
  "contacts": [ Contact ],     // src/models/contact.js
  "timeline": [ { "id", "dateLabel", "title", "author", "description", "icon", "status" } ],
  "owner": Representative | null,
  "funnel": { "currentStage", "daysInCurrentStage", "enteredStageAt" },
  "cadence": { "status", "lastContactLabel", "nextContactLabel", "progress",
               "nextAction": { "title", "whenLabel" } | null,
               "plan": [ { "title", "whenLabel", "state" } ] }
}
// 404 → { "message": "Empresa não encontrado." }

// POST /leads/:id/transfer
{ "representativeId": "rep-2", "reason": "..." }   → { "ok": true }

// POST /leads/:id/notes
{ "title", "description", "stage"? }               → { "id": "note-new" }
```

### Contatos

| Método | Rota | Uso |
| --- | --- | --- |
| GET | `/contacts` | Tela Contatos |

```jsonc
{ "items": [ Contact ] }
```

### Representantes

| Método | Rota | Uso |
| --- | --- | --- |
| GET | `/representatives` | Tela Equipe |
| GET | `/representatives/:id` | Detalhe do representante |
| POST | `/representatives` | Wizard "Novo representante" |

```jsonc
// GET /representatives → 200
{ "items": [ Representative ], "stats": [ Stat ] }

// GET /representatives/:id → 200
{
  "representative": Representative,
  "notice": { "title", "description" } | null,
  "stats": [ Stat ],
  "evolution": [ { "month", "value" } ],
  "leads": [ Lead ]
}
// 404 → { "message": "Representante não encontrado." }
```

### Agenda

| Método | Rota | Uso |
| --- | --- | --- |
| GET | `/agenda` | Tela Agenda (params: `month` = `yyyy-mm`) |
| POST | `/appointments` | Modal "Novo compromisso" |

```jsonc
// GET /agenda → 200
{
  "items": [ Appointment ],   // src/models/appointment.js
  "overview": [ { "key", "icon", "tone", "label", "value" } ],
  "freeSlots": [ "09:00", "10:30" ]
}
```

### Notificações

| Método | Rota | Uso |
| --- | --- | --- |
| GET | `/notifications` | Sino do cabeçalho |

```jsonc
{ "items": [ { "id", "icon", "title", "description", "tone", "createdAt", "read" } ],
  "unread": 2 }
```

### Minha conta

| Método | Rota | Uso |
| --- | --- | --- |
| GET | `/me/profile` | Aba Conta |
| PUT | `/me/profile` | Salvar dados pessoais |
| PUT | `/me/preferences` | Salvar preferências |
| PUT | `/me/password` | Alterar senha |

```jsonc
// GET/PUT /me/profile → 200
{ "name", "email", "whatsapp", "role", "address", "cityState" }

// PUT /me/preferences
{ "newLeads": true, "appointmentReminders": true,
  "lateCadenceAlerts": true, "weeklySummaryEmail": false }   → prefs atualizadas

// PUT /me/password
{ "currentPassword", "newPassword" }   → { "ok": true }
```

## Formato de erro

O interceptor do Axios (`src/services/http.js`) converte qualquer erro em
`Error` com mensagem legível, priorizando `response.data.message`:

```jsonc
// 4xx/5xx
{ "message": "Empresa não encontrado." }
```

Mensagens amigáveis para timeout e falha de conexão são geradas no cliente.

## Autenticação (quando definida)

Quando o backend definir o mecanismo, os pontos de integração já existem:
`src/services/http.js` (interceptor/request para injetar credenciais) e
`src/viewmodels/sessionContext.js` + `useSession.jsx` (estado de sessão).
