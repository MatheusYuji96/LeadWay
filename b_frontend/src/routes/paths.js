/** Caminhos nomeados da aplicação. */
export const paths = {
  landing: '/',
  login: '/entrar',
  signup: '/cadastro',
  app: '/app',
  dashboard: '/app',
  leads: '/app/leads',
  leadDetail: (leadId) => `/app/leads/${leadId}`,
  contacts: '/app/contatos',
  team: '/app/equipe',
  repDetail: (repId) => `/app/equipe/${repId}`,
  agenda: '/app/agenda',
  settings: '/app/configuracoes',
  states: '/app/estados',
}

export default paths
