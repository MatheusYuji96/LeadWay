// Dashboard de demonstração: indicadores e gráficos NÃO são dados do MySQL.
// Mantidos locais até existir um contrato de API apropriado para o painel.
import {
  mockDashboardStats, mockWeekActivity, mockTodayAppointments,
  mockTeamStats, mockTeamPerformance, mockStaleLeads, mockRepresentatives,
} from './mock/fixtures'

export const dashboardService = {
  async getOverview() {
    return {
      stats: mockDashboardStats, weekActivity: mockWeekActivity,
      appointments: mockTodayAppointments,
      leadsNeedingAttention: mockStaleLeads.map((lead) => ({ ...lead, daysInStage: lead.days, lastContact: `${lead.days} dias` })),
    }
  },
  async getTeamOverview() {
    return {
      stats: mockTeamStats, performance: mockTeamPerformance,
      staleLeads: mockStaleLeads.map((lead) => ({ ...lead, daysInStage: lead.days, lastContact: `${lead.days} dias` })),
      representatives: mockRepresentatives,
    }
  },
}
