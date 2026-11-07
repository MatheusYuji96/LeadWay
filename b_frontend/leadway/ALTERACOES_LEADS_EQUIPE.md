# Alterações Leads / Equipe

- Lista de Leads: Último contato vem da data mais recente de `GET /leads/{id}/interacoes` (dataHora ou criadoEm). Se a consulta falhar, mostra `—` e registra aviso no console.
- Próxima ação segue `Prospecção / Novo Lead → Qualificação → Apresentação / Proposta → Negociação → Fechado / Ganho → —`.
- Cards: total, todos exceto Fechado/Ganho, Negociação e atenção simulada = 0.
- Cards são calculados da carteira completa, sem alterar quando busca/filtros são aplicados.
- Equipe: mesmo padding horizontal de 16px da tabela de Leads.
- Nenhuma migração SQL necessária. Não execute novamente o script CREATE/DROP do banco: ele apaga dados.
- Atenção: a consulta de histórico é feita uma vez por Lead. Para grandes carteiras, recomenda-se endpoint agregado no backend.
- Teste: npm run build; iniciar Spring Boot e conferir /leads e /leads/{id}/interacoes.
