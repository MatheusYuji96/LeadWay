import { http, unwrap } from './http'

export const dashboardService = {
  /**
   * GET /dashboard/overview
   * Resumo pessoal: stats, compromissos de hoje, atividade da semana e
   * leads que precisam de atenção.
   */
  async getOverview() {
    return unwrap(await http.get('/dashboard/overview'))
  },

  /**
   * GET /dashboard/team
   * Aba "Acompanhamento da equipe": stats, desempenho e leads parados.
   */
  async getTeamOverview() {
    return unwrap(await http.get('/dashboard/team'))
  },
}
