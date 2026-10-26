import { FUNNEL_STAGES } from '@/models/enums'
import {
  mockAgendaOverview,
  mockAppointments,
  mockCompanies,
  mockContacts,
  mockDashboardStats,
  mockFreeSlots,
  mockLeadCadence,
  mockLeadContacts,
  mockLeadFunnel,
  mockLeadStats,
  mockTimeline,
  mockNotifications,
  mockPreferences,
  mockProfile,
  mockRepresentatives,
  mockStaleLeads,
  mockTeamDetail,
  mockTeamPageStats,
  mockTeamPerformance,
  mockTeamStats,
  mockTodayAppointments,
  mockUser,
  mockWeekActivity,
} from './fixtures'

const MOCK_DELAY_MS = 350

const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms))

function parseBody(config) {
  if (!config.data) return {}
  if (typeof config.data === 'string') {
    try {
      return JSON.parse(config.data)
    } catch {
      return {}
    }
  }
  return config.data
}

function notFound(resource, config) {
  const error = new Error(`${resource} não encontrado.`)
  error.isAxiosError = true
  error.response = {
    status: 404,
    statusText: 'Not Found',
    data: { message: `${resource} não encontrado.` },
    config,
  }
  throw error
}

function buildRoutes() {
  return [
    ['post', /^\/auth\/login$/, () => ({ user: mockUser })],
    ['post', /^\/auth\/signup$/, () => ({ user: mockUser })],

    [
      'get',
      /^\/dashboard\/overview$/,
      () => ({
        stats: mockDashboardStats,
        appointments: mockTodayAppointments,
        weekActivity: mockWeekActivity,
        leadsNeedingAttention: mockCompanies.slice(0, 4),
      }),
    ],
    [
      'get',
      /^\/dashboard\/team$/,
      () => ({
        stats: mockTeamStats,
        performance: mockTeamPerformance,
        staleLeads: mockStaleLeads,
        representatives: mockRepresentatives,
      }),
    ],

    [
      'get',
      /^\/leads$/,
      (params) => {
        const search = String(params?.search ?? '').trim().toLowerCase()
        const stage = params?.stage || ''
        const cadence = params?.cadence || ''
        let items = [...mockCompanies]
        if (search) {
          items = items.filter((c) =>
            [c.name, c.cnpj, c.representativeName].some((v) => v.toLowerCase().includes(search)),
          )
        }
        if (stage) items = items.filter((c) => c.stage === stage)
        if (cadence) items = items.filter((c) => c.cadenceStatus === cadence)
        return { items, total: items.length, stats: mockLeadStats }
      },
    ],
    [
      'get',
      /^\/leads\/([^/]+)$/,
      (params, config, matches) => {
        const lead = mockCompanies.find((c) => c.id === matches[1])
        if (!lead) notFound('Empresa', config)
        const rep = mockRepresentatives.find((r) => r.id === lead.representativeId)
        const contactIds = mockLeadContacts[lead.id] ?? mockContacts.filter((c) => c.companyId === lead.id).map((c) => c.id)
        const contacts = contactIds
          .map((id) => mockContacts.find((c) => c.id === id))
          .filter(Boolean)
        const funnel = mockLeadFunnel[lead.id] ?? {
          currentStage: FUNNEL_STAGES.includes(lead.stage) ? lead.stage : FUNNEL_STAGES[0],
          daysInCurrentStage: lead.daysInStage ?? 0,
          enteredStageAt: lead.enteredStageAt,
        }
        const cadence = mockLeadCadence[lead.id] ?? {
          status: lead.cadenceStatus,
          lastContactLabel: lead.lastContact,
          nextContactLabel: lead.nextAction,
          progress: 40,
          nextAction: null,
          plan: [],
        }
        return {
          lead,
          contacts,
          timeline: mockTimeline[lead.id] ?? [],
          owner: rep ?? null,
          funnel,
          cadence,
        }
      },
    ],
    ['post', /^\/leads$/, () => ({ id: 'lead-new' })],
    ['put', /^\/leads\/([^/]+)$/, () => ({ ok: true })],
    ['post', /^\/leads\/([^/]+)\/transfer$/, () => ({ ok: true })],
    ['post', /^\/leads\/([^/]+)\/notes$/, () => ({ id: 'note-new' })],

    ['get', /^\/contacts$/, () => ({ items: mockContacts })],

    [
      'get',
      /^\/representatives$/,
      () => ({ items: mockRepresentatives, stats: mockTeamPageStats }),
    ],
    [
      'get',
      /^\/representatives\/([^/]+)$/,
      (params, config, matches) => {
        const rep = mockRepresentatives.find((r) => r.id === matches[1])
        if (!rep) notFound('Representante', config)
        const detail = mockTeamDetail[rep.id]
        return {
          representative: rep,
          notice: detail?.notice ?? null,
          stats: detail?.stats ?? [],
          evolution: detail?.evolution ?? [],
          leads: mockCompanies.filter((c) => c.representativeId === rep.id),
        }
      },
    ],
    ['post', /^\/representatives$/, () => ({ id: 'rep-new' })],

    [
      'get',
      /^\/agenda$/,
      (params) => {
        const month = params?.month // yyyy-mm
        const items = month
          ? mockAppointments.filter((a) => a.date.startsWith(month))
          : mockAppointments
        return {
          items,
          overview: mockAgendaOverview,
          freeSlots: mockFreeSlots,
        }
      },
    ],
    ['post', /^\/appointments$/, () => ({ id: 'evt-new' })],

    ['get', /^\/notifications$/, () => ({ items: mockNotifications, unread: mockNotifications.filter((n) => !n.read).length })],

    ['get', /^\/me\/profile$/, () => mockProfile],
    ['put', /^\/me\/profile$/, () => mockProfile],
    ['put', /^\/me\/preferences$/, (params, config) => ({ ...mockPreferences, ...parseBody(config) })],
    ['put', /^\/me\/password$/, () => ({ ok: true })],
  ]
}

/**
 * Adaptador Axios local usado quando VITE_USE_MOCK=true.
 * Reproduz os contratos documentados em docs/API_CONTRACTS.md com fixtures
 * do protótipo. Nenhum dado real de backend é envolvido.
 */
export function createMockAdapter() {
  const routes = buildRoutes()
  return async function mockAdapter(config) {
    const method = (config.method || 'get').toLowerCase()
    const url = (config.url || '').split('?')[0]
    const params = config.params ?? {}

    await delay(MOCK_DELAY_MS)

    for (const [routeMethod, pattern, handler] of routes) {
      if (routeMethod !== method) continue
      const matches = url.match(pattern)
      if (!matches) continue
      const data = handler(params, config, matches)
      return {
        data,
        status: 200,
        statusText: 'OK',
        headers: {},
        config,
        request: null,
      }
    }

    const error = new Error(`Rota de demonstração não implementada: ${method.toUpperCase()} ${config.url}`)
    error.isAxiosError = true
    error.response = {
      status: 404,
      statusText: 'Not Found',
      data: { message: 'Endpoint ainda não disponível no backend e sem cobertura no modo demonstração.' },
      config,
    }
    throw error
  }
}
