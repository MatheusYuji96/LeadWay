import { http, unwrap, isMockMode } from './http'
import {
  mockDashboardStats, mockWeekActivity, mockTodayAppointments,
  mockTeamStats, mockTeamPerformance, mockStaleLeads, mockRepresentatives,
} from './mock/fixtures'

const number = (value) => Number.isFinite(Number(value)) ? Number(value) : 0
const dateOf = (value) => value ? new Date(value) : null
const daysSince = (value) => {
  const date = dateOf(value)
  return date && !Number.isNaN(date.getTime())
    ? Math.max(0, Math.floor((Date.now() - date.getTime()) / 86400000))
    : null
}
const dayName = (date) => new Intl.DateTimeFormat('pt-BR', { weekday: 'short', timeZone: 'UTC' }).format(new Date(`${date}T12:00:00Z`)).replace('.', '')

export const dashboardService = {
  async getOverview(vendedorId) {
    if (isMockMode) {
      return {
        stats: mockDashboardStats, weekActivity: mockWeekActivity,
        appointments: mockTodayAppointments,
        leadsNeedingAttention: mockStaleLeads.map((lead) => ({ ...lead, daysInStage: lead.days, lastContact: `${lead.days} dias` })),
      }
    }
    const id = Number(vendedorId)
    if (!Number.isSafeInteger(id) || id <= 0) {
      throw new Error('Não foi possível identificar o vendedor da sessão. Entre novamente na sua conta.')
    }
    const base = '/dashboard'
    const [indicatorsResponse, activitiesResponse, appointmentsResponse, attentionResponse] = await Promise.all([
      http.get(`${base}/indicadores/${id}`),
      http.get(`${base}/atividades-semana/${id}`),
      http.get(`${base}/proximos-compromissos/${id}`),
      http.get(`${base}/leads-atencao/${id}`),
    ])
    const indicators = unwrap(indicatorsResponse) ?? {}
    const activities = unwrap(activitiesResponse)?.atividades ?? []
    const appointments = unwrap(appointmentsResponse) ?? []
    const attention = unwrap(attentionResponse) ?? []
    return {
      stats: [
        { key: 'total', icon: 'building', label: 'Total de leads', value: String(number(indicators.totaLeads ?? indicators.totalLeads)), detail: 'ativos na carteira', tone: 'brand' },
        { key: 'appointments', icon: 'calendar', label: 'Compromissos', value: String(number(indicators.compromissosSemana)), detail: 'nesta semana', tone: 'blue' },
        { key: 'prospecting', icon: 'trend', label: 'Em prospecção', value: String(number(indicators.leadsEmProspeccao)), detail: 'sem primeira visita', tone: 'purple' },
        { key: 'stale', icon: 'bell', label: 'Sem atualização', value: String(number(indicators.leadsSemAtualizacao)), detail: 'indicador informado pelo backend', tone: 'danger' },
      ],
      weekActivity: activities.map((item) => ({
        day: dayName(item.dia), contacts: number(item.contatos),
        meetings: number(item.portfolios), followUps: number(item.followUps),
      })),
      appointments: appointments.map((item, index) => {
        const date = dateOf(item.horario)
        return {
          id: `${item.horario ?? index}-${index}`,
          time: date && !Number.isNaN(date.getTime()) ? date.toLocaleString('pt-BR', { day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit' }) : '—',
          type: item.descricao || 'Compromisso', companyName: item.nomeLead || 'Empresa não informada',
          contactName: '', tone: 'brand',
        }
      }),
      leadsNeedingAttention: attention.map((item) => {
        const days = daysSince(item.ultimoContato)
        return {
          id: item.leadId, name: item.nomeLead || 'Lead sem nome',
          representativeName: item.proximaAcao || 'Sem próxima ação', stage: item.etapa || 'Sem etapa',
          daysInStage: days ?? 0,
          lastContact: days === null ? 'Sem contato registrado' : `${days} dia(s) sem contato`,
        }
      }),
    }
  },
  async getTeamOverview() {
    if (isMockMode) {
      return {
        stats: mockTeamStats, performance: mockTeamPerformance,
        staleLeads: mockStaleLeads.map((lead) => ({ ...lead, daysInStage: lead.days, lastContact: `${lead.days} dias` })),
        representatives: mockRepresentatives,
      }
    }
    const data = unwrap(await http.get('/dashboard/equipe')) ?? {}
    return {
      stats: [
        { key: 'reps', icon: 'users', label: 'Representantes ativos', value: String(number(data.representantesAtivos)), detail: 'cadastrados e ativos', tone: 'brand' },
        { key: 'leads', icon: 'building', label: 'Leads em carteira', value: String(number(data.leadsEmCarteira)), detail: 'leads ativos', tone: 'blue' },
        { key: 'contacts', icon: 'check', label: 'Contatos realizados', value: String(number(data.contatosRealizados)), detail: 'leads na etapa 3', tone: 'purple' },
        { key: 'pending', icon: 'bell', label: 'Pendências da equipe', value: String(number(data.pendenciasEquipe)), detail: 'sem atualização há mais de 7 dias', tone: 'danger' },
      ],
      performance: (data.desempenho ?? []).map((item) => ({
        id: item.id, name: item.nome, contacts: number(item.contatos),
        meetings: number(item.propostas), visits: number(item.leads),
      })),
      representatives: (data.representantes ?? []).map((item) => ({
        id: item.id, name: item.nome, initials: (item.nome ?? '').split(' ').slice(0, 2).map((p) => p[0]).join('').toUpperCase(),
        code: item.codigo || '—', pending: number(item.pendencias),
      })),
      staleLeads: (data.leadsParados ?? []).map((item) => ({
        id: item.id, name: item.nome, representativeName: item.responsavel,
        stage: item.etapa, days: number(item.diasParado),
      })),
    }
  },
}
