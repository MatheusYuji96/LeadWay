# LeadWay — Pré-vendas

Aplicação de gestão de leads para representantes comerciais, construída em React + Vite
(JSX), com roteamento, estilos em CSS Modules, Axios e arquitetura MVVM.

## Rodando

```bash
npm install
npm run dev      # desenvolvimento
npm run build    # build de produção
npm run lint     # ESLint
```

## Configuração

Variáveis em `.env` (ver `.env.example`):

| Variável | Descrição |
| --- | --- |
| `VITE_API_BASE_URL` | Base URL da API. Ausente → modo demonstração. |
| `VITE_USE_MOCK` | `true` força o mock; `false` exige API real. |

Sem `VITE_API_BASE_URL`, o app usa um adaptador Axios local com dados de
demonstração derivados do protótipo — um banner avisa que nada vem de backend.
Os contratos consumidos estão documentados em [`docs/API_CONTRACTS.md`](docs/API_CONTRACTS.md).

## Estrutura

```
src/
  components/
    common/     # Button, Field, Modal, Panel, Feedback (loading/erro/vazio)...
    layout/     # Sidebar, AppHeader, AppLayout, notificações
    domain/     # LeadTable, RepTable, Timeline, FunnelCard, wizards...
  models/       # entidades e enums (funil, cadência, tipos de agenda)
  services/     # Axios + camada de serviço por recurso (+ mock)
  viewmodels/   # hooks MVVM por tela (useXxxViewModel) e sessão
  views/        # páginas (rotas)
  routes/       # paths nomeados
  styles/       # tokens globais e classes de página compartilhadas
```

Rotas: `/` (institucional), `/entrar`, `/cadastro` e `/app/**`
(dashboard, leads, leads/:id, contatos, equipe, equipe/:id, agenda,
configuracoes, estados).
