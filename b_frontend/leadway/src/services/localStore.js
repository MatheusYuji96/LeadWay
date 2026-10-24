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
  mockNotifications,
  mockPreferences,
  mockProfile,
  mockRepresentatives,
  mockStaleLeads,
  mockTeamDetail,
  mockTeamPageStats,
  mockTeamPerformance,
  mockTeamStats,
  mockTimeline,
  mockTodayAppointments,
  mockUser,
  mockWeekActivity,
} from './mock/fixtures'

const STORAGE_KEY = 'leadway_local_store_v1'
const INITIALIZED_KEY = 'leadway_local_store_initialized_v1'

function deepClone(obj) {
  return JSON.parse(JSON.stringify(obj))
}

function getInitialData() {
  return {
    users: [],
    authSession: null, // { user: {...}, token?: string }
    companies: deepClone(mockCompanies),
    representatives: deepClone(mockRepresentatives),
    contacts: deepClone(mockContacts),
    leadContacts: deepClone(mockLeadContacts),
    leadFunnel: deepClone(mockLeadFunnel),
    leadCadence: deepClone(mockLeadCadence),
    timeline: deepClone(mockTimeline),
    appointments: deepClone(mockAppointments),
    notifications: deepClone(mockNotifications),
    profile: deepClone(mockProfile),
    preferences: deepClone(mockPreferences),
    // aggregates/stats can be computed on-the-fly
    stats: {
      dashboardStats: deepClone(mockDashboardStats),
      todayAppointments: deepClone(mockTodayAppointments),
      weekActivity: deepClone(mockWeekActivity),
      teamStats: deepClone(mockTeamStats),
      teamPerformance: deepClone(mockTeamPerformance),
      staleLeads: deepClone(mockStaleLeads),
      teamPageStats: deepClone(mockTeamPageStats),
      teamDetail: deepClone(mockTeamDetail),
      leadStats: deepClone(mockLeadStats),
      agendaOverview: deepClone(mockAgendaOverview),
      freeSlots: deepClone(mockFreeSlots),
    },
  }
}

function load() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (raw) {
      const parsed = JSON.parse(raw)
      // Ensure all keys exist
      const initial = getInitialData()
      return { ...initial, ...parsed }
    }
  } catch (e) {
    // ignore
  }
  return null
}

function save(data) {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(data))
  } catch (e) {
    // ignore
  }
}

export function getStore() {
  let store = load()
  if (!store) {
    // Initialize with fixtures only once - but per requirements:
    // "Não deixe os registros fictícios sobrescreverem os dados cadastrados ou fazerem registros excluídos reaparecerem."
    // Also "As informações cadastradas devem continuar disponíveis após atualizar a página ou fechar e abrir o navegador."
    // Initialize store with fixtures if it doesn't exist yet (first run). Once initialized, we keep whatever is in localStorage.
    // We mark initialized; subsequent loads read from localStorage. Deleted items won't reappear because they're removed from localStorage.
    store = getInitialData()
    save(store)
    try {
      localStorage.setItem(INITIALIZED_KEY, '1')
    } catch (e) {}
    return store
  }
  return store
}

export function updateStore(mutator) {
  const store = getStore()
  const result = mutator(store) || store
  save(store)
  return result
}

export function resetStore() {
  try {
    localStorage.removeItem(STORAGE_KEY)
    localStorage.removeItem(INITIALIZED_KEY)
  } catch (e) {}
}
