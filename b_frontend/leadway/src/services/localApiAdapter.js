import { getStore, updateStore } from './localStore'
import { userInitials } from '@/models/user'

const MOCK_DELAY_MS = 150

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

function genId(prefix = 'id') {
  return `${prefix}-${Date.now()}-${Math.random().toString(36).slice(2, 9)}`
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
    // Auth
    [
      'post',
      /^\/auth\/login$/,
      (params, config) => {
        const body = parseBody(config)
        const store = getStore()
        const email = String(body.email || '').trim().toLowerCase()
        const password = String(body.password || '')
        const user = store.users.find((u) => String(u.email || '').toLowerCase() === email && u.password === password)
        if (!user) {
          const error = new Error('E-mail ou senha inválidos.')
          error.isAxiosError = true
          error.response = { status: 401, statusText: 'Unauthorized', data: { message: 'E-mail ou senha inválidos.' }, config }
          throw error
        }
        const { password: _, ...safeUser } = user
        const sessionUser = {
          id: safeUser.id,
          name: safeUser.name,
          email: safeUser.email,
          role: safeUser.role || 'Usuário',
          initials: userInitials(safeUser.name),
        }
        updateStore((s) => {
          s.authSession = { user: sessionUser }
        })
        return { user: sessionUser }
      },
    ],
    [
      'post',
      /^\/auth\/signup$/,
      (params, config) => {
        const body = parseBody(config)
        const store = getStore()
        const email = String(body.email || '').trim().toLowerCase()
        if (store.users.some((u) => String(u.email || '').toLowerCase() === email)) {
          const error = new Error('Já existe uma conta com este e-mail.')
          error.isAxiosError = true
          error.response = { status: 409, statusText: 'Conflict', data: { message: 'Já existe uma conta com este e-mail.' }, config }
          throw error
        }
        const newUser = {
          id: genId('user'),
          name: String(body.name || '').trim(),
          email: String(body.email || '').trim(),
          whatsapp: String(body.whatsapp || '').trim(),
          password: String(body.password || ''),
          role: 'Usuário',
        }
        const sessionUser = {
          id: newUser.id,
          name: newUser.name,
          email: newUser.email,
          role: newUser.role,
          initials: userInitials(newUser.name),
        }
        updateStore((s) => {
          s.users.push(newUser)
          s.authSession = { user: sessionUser }
        })
        return { user: sessionUser }
      },
    ],
    [
      'get',
      /^\/auth\/me$/,
      () => {
        const store = getStore()
        if (!store.authSession?.user) {
          const error = new Error('Não autenticado')
          error.isAxiosError = true
          error.response = { status: 401, statusText: 'Unauthorized', data: { message: 'Não autenticado' }, config: {} }
          throw error
        }
        return { user: store.authSession.user }
      },
    ],
    [
      'post',
      /^\/auth\/logout$/,
      () => {
        updateStore((s) => {
          s.authSession = null
        })
        return { ok: true }
      },
    ],

    // Dashboard
    [
      'get',
      /^\/dashboard\/overview$/,
      () => {
        const store = getStore()
        const companies = store.companies || []
        const appointments = store.appointments || []
        const todayAppts = appointments.filter((a) => a.date === '2025-05-15')
        const stats = [
          { key: 'total', icon: 'building', label: 'Total de leads', value: String(companies.length), detail: 'na carteira', delta: '', tone: 'brand' },
          { key: 'appointments', icon: 'calendar', label: 'Compromissos', value: String(todayAppts.length || store.stats?.todayAppointments?.length || 0), detail: 'hoje', delta: '', tone: 'blue' },
          { key: 'prospecting', icon: 'trend', label: 'Em prospecção', value: String(companies.filter((c) => c.stage === 'Primeiro Contato' || c.stage === 'Qualificação').length), detail: 'em andamento', delta: '', tone: 'purple' },
          { key: 'stale', icon: 'bell', label: 'Sem atualização', value: String(companies.filter((c) => (c.daysInStage || 0) > 7).length), detail: 'há mais de 7 dias', delta: '', tone: 'danger' },
        ]
        return {
          stats,
          appointments: store.stats?.todayAppointments || [],
          weekActivity: store.stats?.weekActivity || [],
          leadsNeedingAttention: companies.filter((c) => (c.daysInStage || 0) > 7).slice(0, 4),
        }
      },
    ],
    [
      'get',
      /^\/dashboard\/team$/,
      () => {
        const store = getStore()
        const reps = store.representatives || []
        const companies = store.companies || []
        const stats = [
          { key: 'reps', icon: 'users', label: 'Representantes ativos', value: String(reps.filter((r) => r.active).length), detail: 'ativos', delta: '', tone: 'brand' },
          { key: 'wallet', icon: 'briefcase', label: 'Leads em carteira', value: String(companies.length), detail: 'total', delta: '', tone: 'blue' },
          { key: 'contacts', icon: 'check', label: 'Contatos realizados', value: String(reps.reduce((s, r) => s + (r.contacts || 0), 0)), detail: 'no total', delta: '', tone: 'purple' },
          { key: 'pending', icon: 'bell', label: 'Pendências da equipe', value: String(reps.reduce((s, r) => s + (r.pending || 0), 0)), detail: 'exigem atenção', delta: '', tone: 'danger' },
        ]
        return {
          stats,
          performance: store.stats?.teamPerformance || [],
          staleLeads: companies
            .filter((c) => (c.daysInStage || 0) > 7)
            .map((c) => ({
              id: c.id,
              name: c.name,
              representativeName: c.representativeName,
              stage: c.stage,
              days: c.daysInStage || 0,
            }))
            .slice(0, 5),
          representatives: reps,
        }
      },
    ],

    // Leads
    [
      'get',
      /^\/leads$/,
      (params) => {
        const store = getStore()
        const search = String(params?.search ?? '').trim().toLowerCase()
        const stage = params?.stage || ''
        const cadence = params?.cadence || ''
        let items = [...(store.companies || [])]
        if (search) {
          items = items.filter((c) => [c.name, c.cnpj, c.representativeName].some((v) => String(v || '').toLowerCase().includes(search)))
        }
        if (stage) items = items.filter((c) => c.stage === stage)
        if (cadence) items = items.filter((c) => c.cadenceStatus === cadence)
        const all = store.companies || []
        const stats = {
          total: all.length,
          prospecting: all.filter((c) => c.stage === 'Primeiro Contato' || c.stage === 'Qualificação').length,
          followUp: all.filter((c) => c.stage === 'Follow-up').length,
          needAttention: all.filter((c) => (c.daysInStage || 0) > 7).length,
        }
        return { items, total: items.length, stats }
      },
    ],
    [
      'get',
      /^\/leads\/([^/]+)$/,
      (params, config, matches) => {
        const store = getStore()
        const leadId = matches[1]
        const lead = (store.companies || []).find((c) => c.id === leadId)
        if (!lead) notFound('Empresa', config)
        const rep = (store.representatives || []).find((r) => r.id === lead.representativeId)
        const contactIds = store.leadContacts?.[lead.id] ?? (store.contacts || []).filter((c) => c.companyId === lead.id).map((c) => c.id)
        const contacts = contactIds.map((id) => (store.contacts || []).find((c) => c.id === id)).filter(Boolean)
        const funnel = store.leadFunnel?.[lead.id] ?? {
          currentStage: lead.stage,
          daysInCurrentStage: lead.daysInStage ?? 0,
          enteredStageAt: lead.enteredStageAt,
        }
        const cadence = store.leadCadence?.[lead.id] ?? {
          status: lead.cadenceStatus,
          lastContactLabel: lead.lastContact,
          nextContactLabel: lead.nextAction,
        }
        return {
          lead,
          contacts,
          timeline: store.timeline?.[lead.id] ?? [],
          owner: rep ?? null,
          funnel,
          cadence,
        }
      },
    ],
    [
      'post',
      /^\/leads$/,
      (params, config) => {
        const body = parseBody(config)
        const id = genId('lead')
        const storeNow = getStore()
        const repId = body.representativeId || (storeNow.representatives || [])[0]?.id || null
        const rep = (storeNow.representatives || []).find((r) => r.id === repId)
        const now = new Date()
        const newLead = {
          id,
          name: body.name || body.companyName || '',
          cnpj: body.cnpj || '',
          sector: body.segment || body.sector || '',
          score: body.score || 3,
          stage: body.stage || 'Qualificação',
          representativeId: repId,
          representativeName: rep?.name || body.representativeName || '',
          lastContact: `Hoje, ${now.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })}`,
          nextAction: body.nextAction || 'Definir próximo contato',
          cadenceStatus: body.cadenceStatus || 'NEXT',
          phone: body.phone || '',
          whatsapp: body.whatsapp || body.contactPhone || '',
          email: body.email || body.contactEmail || '',
          address: body.address || {
            street: body.street || '',
            number: body.number || '',
            complement: body.complement || '',
            neighborhood: body.neighborhood || '',
            city: body.city || '',
            state: body.state || '',
            zipCode: body.zipCode || '',
          },
          enteredStageAt: now.toISOString().slice(0, 10),
          daysInStage: 0,
          website: body.site || body.website,
        }
        updateStore((s) => {
          if (!s.companies) s.companies = []
          s.companies.push(newLead)
          if (!s.leadFunnel) s.leadFunnel = {}
          if (!s.leadCadence) s.leadCadence = {}
          s.leadFunnel[id] = { currentStage: newLead.stage, daysInCurrentStage: 0, enteredStageAt: newLead.enteredStageAt }
          s.leadCadence[id] = { status: newLead.cadenceStatus, lastContactLabel: newLead.lastContact, nextContactLabel: newLead.nextAction }
          if (body.contactName) {
            if (!s.contacts) s.contacts = []
            if (!s.leadContacts) s.leadContacts = {}
            const cid = genId('contact')
            s.contacts.push({
              id: cid,
              name: body.contactName,
              role: body.contactRole || '',
              email: body.contactEmail || '',
              whatsapp: body.contactPhone || '',
              companyId: id,
              companyName: newLead.name,
              initials: String(body.contactName || '')
                .split(/\s+/)
                .slice(0, 2)
                .map((p) => p[0])
                .join('')
                .toUpperCase(),
              status: 'active',
            })
            s.leadContacts[id] = [cid]
          }
        })
        return { id }
      },
    ],
    [
      'put',
      /^\/leads\/([^/]+)$/,
      (params, config, matches) => {
        const body = parseBody(config)
        const leadId = matches[1]
        updateStore((s) => {
          if (!s.companies) s.companies = []
          const idx = s.companies.findIndex((c) => c.id === leadId)
          if (idx >= 0) {
            s.companies[idx] = { ...s.companies[idx], ...body }
            if (body.representativeId) {
              const rep = (s.representatives || []).find((r) => r.id === body.representativeId)
              if (rep) s.companies[idx].representativeName = rep.name
            }
          }
          if (body.stage) {
            if (!s.leadFunnel) s.leadFunnel = {}
            s.leadFunnel[leadId] = { ...(s.leadFunnel[leadId] || {}), currentStage: body.stage, daysInCurrentStage: 0, enteredStageAt: new Date().toISOString().slice(0, 10) }
            if (idx >= 0) {
              s.companies[idx].stage = body.stage
              s.companies[idx].enteredStageAt = s.leadFunnel[leadId].enteredStageAt
              s.companies[idx].daysInStage = 0
            }
          }
        })
        return { ok: true }
      },
    ],
    [
      'delete',
      /^\/leads\/([^/]+)$/,
      (params, config, matches) => {
        const leadId = matches[1]
        updateStore((s) => {
          s.companies = (s.companies || []).filter((c) => c.id !== leadId)
          if (s.leadFunnel) delete s.leadFunnel[leadId]
          if (s.leadCadence) delete s.leadCadence[leadId]
          if (s.timeline) delete s.timeline[leadId]
          if (s.leadContacts) delete s.leadContacts[leadId]
        })
        return { ok: true }
      },
    ],
    ['post', /^\/leads\/([^/]+)\/transfer$/, () => ({ ok: true })],
    ['post', /^\/leads\/([^/]+)\/notes$/, () => ({ id: 'note-new' })],

    // Contacts
    ['get', /^\/contacts$/, () => ({ items: getStore().contacts || [] })],

    // Representatives
    [
      'get',
      /^\/representatives$/,
      () => {
        const store = getStore()
        const items = store.representatives || []
        const companies = store.companies || []
        const stats = [
          { key: 'reps', icon: 'users', label: 'Representantes', value: String(items.length), detail: 'cadastrados', delta: '', tone: 'brand' },
          { key: 'distributed', icon: 'briefcase', label: 'Leads distribuídos', value: String(companies.length), detail: 'na carteira', delta: '', tone: 'blue' },
          { key: 'contacts', icon: 'trend', label: 'Contatos no mês', value: String(items.reduce((s, r) => s + (r.contacts || 0), 0)), detail: 'total', delta: '', tone: 'purple' },
          { key: 'pending', icon: 'bell', label: 'Pendências', value: String(items.reduce((s, r) => s + (r.pending || 0), 0)), detail: 'em aberto', delta: '', tone: 'danger' },
        ]
        return { items, stats }
      },
    ],
    [
      'get',
      /^\/representatives\/([^/]+)$/,
      (params, config, matches) => {
        const store = getStore()
        const repId = matches[1]
        const rep = (store.representatives || []).find((r) => r.id === repId)
        if (!rep) notFound('Representante', config)
        const detail = store.stats?.teamDetail?.[repId] || {
          notice: null,
          stats: [
            { key: 'wallet', icon: 'briefcase', label: 'Leads em carteira', value: String((store.companies || []).filter((c) => c.representativeId === repId).length), detail: '', delta: '', tone: 'brand' },
            { key: 'contacts', icon: 'phone', label: 'Contatos realizados', value: String(rep.contacts || 0), detail: '', delta: '', tone: 'blue' },
            { key: 'visits', icon: 'calendar', label: 'Visitas', value: String(rep.visits || 0), detail: '', delta: '', tone: 'purple' },
            { key: 'conversions', icon: 'trend', label: 'Conversões', value: String(rep.conversions || 0), detail: '', delta: '', tone: 'warning' },
          ],
          evolution: store.stats?.teamDetail?.['rep-1']?.evolution || [],
        }
        return {
          representative: rep,
          notice: detail.notice ?? null,
          stats: detail.stats ?? [],
          evolution: detail.evolution ?? [],
          leads: (store.companies || []).filter((c) => c.representativeId === repId),
        }
      },
    ],
    [
      'post',
      /^\/representatives$/,
      (params, config) => {
        const body = parseBody(config)
        const id = genId('rep')
        const newRep = {
          id,
          name: body.name || '',
          code: body.code || `VEN-${String(Math.floor(Math.random() * 10000)).padStart(4, '0')}`,
          region: body.region || '',
          email: body.email || '',
          phone: body.phone || '',
          initials: String(body.name || '')
            .trim()
            .split(/\s+/)
            .slice(0, 2)
            .map((p) => p[0])
            .join('')
            .toUpperCase(),
          leads: 0,
          pending: 0,
          contacts: 0,
          active: body.active !== false,
        }
        updateStore((s) => {
          if (!s.representatives) s.representatives = []
          s.representatives.push(newRep)
        })
        return { id }
      },
    ],
    [
      'delete',
      /^\/representatives\/([^/]+)$/,
      (params, config, matches) => {
        const repId = matches[1]
        updateStore((s) => {
          s.representatives = (s.representatives || []).filter((r) => r.id !== repId)
        })
        return { ok: true }
      },
    ],

    // Agenda
    [
      'get',
      /^\/agenda$/,
      (params) => {
        const store = getStore()
        const month = params?.month
        const items = month ? (store.appointments || []).filter((a) => a.date.startsWith(month)) : store.appointments || []
        return {
          items,
          overview: store.stats?.agendaOverview || [],
          freeSlots: store.stats?.freeSlots || [],
        }
      },
    ],
    ['post', /^\/appointments$/, () => ({ id: 'evt-new' })],

    // Notifications
    ['get', /^\/notifications$/, () => ({ items: getStore().notifications || [], unread: (getStore().notifications || []).filter((n) => !n.read).length })],

    // Settings
    ['get', /^\/me\/profile$/, () => getStore().profile || {}],
    [
      'put',
      /^\/me\/profile$/,
      (params, config) => {
        const body = parseBody(config)
        updateStore((s) => {
          s.profile = { ...(s.profile || {}), ...body }
        })
        return getStore().profile
      },
    ],
    [
      'put',
      /^\/me\/preferences$/,
      (params, config) => {
        const body = parseBody(config)
        updateStore((s) => {
          s.preferences = { ...(s.preferences || {}), ...body }
        })
        return getStore().preferences
      },
    ],
    ['put', /^\/me\/password$/, () => ({ ok: true })],
  ]
}

export function createLocalApiAdapter() {
  const routes = buildRoutes()
  return async function localApiAdapter(config) {
    const method = (config.method || 'get').toLowerCase()
    const url = (config.url || '').split('?')[0]
    const params = config.params ?? {}

    await delay(MOCK_DELAY_MS)

    try {
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
    } catch (err) {
      // Re-throw axios-style errors
      if (err?.isAxiosError) throw err
      const error = new Error(err?.message || 'Erro ao processar operação local.')
      error.isAxiosError = true
      error.response = {
        status: 500,
        statusText: 'Internal Server Error',
        data: { message: error.message },
        config,
      }
      throw error
    }

    const error = new Error(`Rota não implementada: ${method.toUpperCase()} ${config.url}`)
    error.isAxiosError = true
    error.response = {
      status: 404,
      statusText: 'Not Found',
      data: { message: 'Endpoint não disponível.' },
      config,
    }
    throw error
  }
}

export default createLocalApiAdapter
