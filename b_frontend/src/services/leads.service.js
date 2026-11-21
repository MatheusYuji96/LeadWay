import { http, unwrap, isMockMode } from './http'
import { contactsService } from './contacts.service'
import { FUNNEL_STAGES } from '@/models/enums'
import { cadencesService } from './cadences.service'
import { cadenceProgress, currentCadence } from '@/utils/cadenceProgress'

const nextFunnelStage = (lead) => {
  const order = Number(lead?.etapa?.ordem)
  const byName = FUNNEL_STAGES.findIndex((stage) => stage === lead?.stage)
  const index = Number.isInteger(order) && order > 0 ? order - 1 : byName
  return index >= 0 && index < FUNNEL_STAGES.length - 1 ? FUNNEL_STAGES[index + 1] : '—'
}

const digits = (value) => String(value ?? '').replace(/\D/g, '')

export function normalizeLead(raw) {
  const addr = raw.endereco ?? raw.address ?? {}
  const name = raw.name ?? raw.nomeFantasia ?? raw.razaoSocial ?? 'Sem nome'
  const owner = raw.responsavel ?? raw.vendedor ?? raw.usuario ?? {}
  const potential = String(raw.potencial ?? '').toUpperCase()
  return {
    ...raw,
    // A resposta atual do backend não expõe o ID interno; CNPJ identifica a linha para navegação.
    id: raw.id ?? null,
    name, cnpj: raw.cnpj ?? '',
    sector: raw.sector ?? raw.setor?.nome ?? '—',
    segment: raw.segment ?? raw.setor?.nome ?? '',
    stage: raw.stage ?? raw.etapa?.nome ?? 'Não definida',
    representativeName: raw.representativeName ?? owner.nome ?? 'Não atribuído',
    representativeId: raw.representativeId ?? raw.responsavelId ?? raw.vendedorId ?? owner.id ?? null,
    score: raw.score ?? (Number(raw.potencial) || { ALTO: 5, MEDIO: 3, BAIXO: 1 }[potential] || 0),
    lastContact: raw.lastContact ?? '—', nextAction: raw.nextAction ?? '—',
    cadenceStatus: raw.cadenceStatus ?? 'UNTRACKED',
    city: raw.city ?? addr.cidade ?? '', state: raw.state ?? addr.estado ?? '',
    address: raw.address ?? { street: addr.rua, number: addr.numero, complement: addr.complemento, neighborhood: addr.bairro, city: addr.cidade, state: addr.estado, zipCode: addr.cep },
    phone: raw.phone ?? raw.telefone ?? '',
  }
}


export const leadsService = {
  /**
   * GET /leads
   * @param {{ search?: string, stage?: string, cadence?: string }} [filters]
   * @returns {Promise<{ items: import('@/models/lead').Lead[], total: number, stats: object }>}
   */
  async list(filters = {}) {
    if (isMockMode) return unwrap(await http.get('/leads', { params: filters }))
    // Busca a carteira completa para que os indicadores não mudem com filtros de tela.
    const result = unwrap(await http.get('/leads'))
    const allItems = (Array.isArray(result) ? result : result?.items ?? []).map(normalizeLead)
      .sort((a, b) => Number(b.ativo !== false) - Number(a.ativo !== false))
    let cadences = []
    try {
      const response = await cadencesService.list()
      cadences = Array.isArray(response) ? response : response?.items ?? []
    } catch (error) {
      console.warn('Não foi possível consultar cadências:', error)
    }
    const stageOrder = FUNNEL_STAGES
    const enriched = await Promise.all(allItems.map(async (lead) => {
      let lastContact = '—'
      if (lead.id != null) {
        try {
          const response = unwrap(await http.get(`/leads/${lead.id}/interacoes`))
          const interactions = Array.isArray(response) ? response : response?.items ?? []
          const dates = interactions.map((entry) => entry.dataHora ?? entry.criadoEm)
            .filter(Boolean).map((date) => new Date(date)).filter((date) => !Number.isNaN(date.getTime()))
          if (dates.length) {
            const latest = new Date(Math.max(...dates.map((date) => date.getTime())))
            lastContact = latest.toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' })
          }
        } catch (error) {
          // A lista de empresas continua disponível se uma consulta de histórico falhar.
          console.warn(`Não foi possível carregar interações do lead ${lead.id}:`, error)
        }
      }
      const position = stageOrder.indexOf(lead.stage)
      const current = currentCadence(cadences.filter(c => String(c.leadId) === String(lead.id)))
      const cadenceStatus = cadenceProgress(current).key
      return { ...lead, cadenceStatus, lastContact, nextAction: position >= 0 && position < stageOrder.length - 1 ? stageOrder[position + 1] : '—' }
    }))
    const stats = {
      total: enriched.filter((lead) => lead.ativo !== false).length,
      prospecting: enriched.filter((lead) => lead.ativo !== false && lead.stage !== 'Fechado / Ganho').length,
      followUp: enriched.filter((lead) => lead.ativo !== false && lead.stage === 'Negociação').length,
      attention: enriched.filter(lead => lead.ativo !== false && ['ATTENTION', 'LATE'].includes(lead.cadenceStatus)).length,
    }
    const term = String(filters.search ?? '').trim().toLocaleLowerCase('pt-BR')
    const items = enriched.filter((lead) => {
      const matchesTerm = !term || [lead.name, lead.cnpj, lead.representativeName].some((value) => String(value ?? '').toLocaleLowerCase('pt-BR').includes(term))
      const matchesStage = !filters.stage || lead.stage === filters.stage
      const matchesOwner = !filters.vendedorId || String(lead.representativeId) === String(filters.vendedorId)
      const matchesCadence = !filters.cadence || lead.cadenceStatus === filters.cadence
      return matchesTerm && matchesStage && matchesOwner && matchesCadence
    })
    return { items, total: items.length, stats }
  },

  /**
   * GET /leads/:id
   * Detalhe completo: lead, contatos, timeline, responsável, funil e cadência.
   * @param {string} id
   */
  async getById(id) {
    if (isMockMode) return unwrap(await http.get(`/leads/${id}`))
    // O controller Java disponibiliza GET /leads, mas não GET /leads/{id}.
    const rows = (await this.list()).items
    const found = rows.find((row) => String(row.id) === String(id))
    if (!found) throw new Error('Empresa não encontrada.')
    const contactResponse = await contactsService.listByLead(id)
    const contacts = (Array.isArray(contactResponse) ? contactResponse : contactResponse?.items ?? []).map((item) => ({
      ...item, name: item.name ?? item.nome, role: item.role ?? item.cargo,
      initials: (item.nome ?? item.name ?? '?').split(' ').slice(0, 2).map((part) => part[0]).join('').toUpperCase(),
    }))
    const ownerData = found.responsavel ?? found.vendedor ?? found.usuario
    const ownerId = ownerData?.id
    const activeCount = ownerId == null ? null : rows.filter((row) =>
      String(row.vendedor?.id ?? row.representativeId) === String(ownerId) && row.ativo !== false,
    ).length
    const owner = ownerData ? {
      name: ownerData.nome, email: ownerData.email, phone: ownerData.telefone,
      initials: (ownerData.nome ?? '?').split(' ').slice(0, 2).map((part) => part[0]).join('').toUpperCase(),
      since: found.vinculadoEm ?? found.vinculado_em ?? found.dataVinculo ?? found.criadoEm ?? found.criado_em ?? null, leads: activeCount,
    } : null
    const interactions = unwrap(await http.get(`/leads/${id}/interacoes`))
    const interactionRows = Array.isArray(interactions) ? interactions : []
    const latest = interactionRows
      .map((item) => item.dataHora ?? item.criadoEm)
      .filter(Boolean)
      .map((value) => new Date(value))
      .filter((date) => !Number.isNaN(date.getTime()))
      .sort((a, b) => b.getTime() - a.getTime())[0]
    const lastContact = latest ? latest.toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' }) : '—'
    const detailedLead = { ...found, lastContact, nextAction: nextFunnelStage(found) }
    const timeline = interactionRows.map((item) => ({
      id: item.id,
      dateLabel: (item.dataHora ?? item.criadoEm) ? new Date(item.dataHora ?? item.criadoEm).toLocaleString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' }) : 'Sem data',
      title: ({ ANOTACAO: 'Anotação', LIGACAO: 'Ligação', REUNIAO: 'Reunião', VISITA: 'Visita', EMAIL: 'E-mail', WHATSAPP: 'WhatsApp' })[item.tipo] ?? item.tipo,
      author: (item.contatoNome ?? item.contato?.nome ?? contacts.find((c) => String(c.id) === String(item.contatoId ?? item.contato?.id))?.nome) ? `Interação com ${item.contatoNome ?? item.contato?.nome ?? contacts.find((c) => String(c.id) === String(item.contatoId ?? item.contato?.id))?.nome}` : 'Interação com a empresa',
      description: item.descricao,
      icon: item.tipo === 'LIGACAO' ? 'phone' : item.tipo === 'REUNIAO' ? 'users' : 'clock',
    }))
    // A data vem exclusivamente de lead_etapa_historico no backend.
    // Para leads antigos sem registro, o backend devolve null e a interface mostra "—".
    let funnel = null
    try {
      funnel = unwrap(await http.get(`/leads/${id}/funil`))
    } catch (error) {
      console.warn(`Não foi possível consultar o histórico do funil do lead ${id}:`, error)
    }
    return { lead: detailedLead, contacts, timeline, owner, funnel, cadence: null }
  },

  /**
   * POST /leads
   * @param {object} payload
   */
  async create(payload) {
    const response = unwrap(await http.post('/leads', payload))
    return isMockMode ? response : normalizeLead(response)
  },

  /**
   * PUT /leads/:id
   * @param {string} id
   * @param {object} payload
   */ 
  async update(id, payload) {
    if (
      !isMockMode &&
      (!Number.isSafeInteger(Number(id)) || Number(id) <= 0)
    ) {
      throw new Error('ID do Lead inválido para atualização.')
    }

    const response = unwrap(
      await http.patch(`/leads/${id}`, payload)
    )

    return isMockMode ? response : normalizeLead(response)
  },

  /**
   * DELETE /leads/:id
   * @param {string} id
   */
  async remove(id) {
    return unwrap(await http.delete(`/leads/${id}`))
  },

  /**
   * POST /leads/:id/transfer
   * @param {string} id
   * @param {{ representativeId: string, reason: string }} payload
   */
  async transfer(id, payload) {
    // O DTO Java atual ainda utiliza vendedorId, embora a coluna MySQL seja responsavel_id.
    if (!isMockMode) return unwrap(await http.patch(`/leads/${id}`, { vendedorId: Number(payload.representativeId) }))
    return unwrap(await http.post(`/leads/${id}/transfer`, payload))
  },

  /**
   * POST /leads/:id/notes
   * @param {string} id
   * @param {{ title: string, description: string, stage?: string }} payload
   */
  async deleteInteraction(id, interactionId) {
    return unwrap(await http.delete(`/leads/${id}/interacoes/${interactionId}`))
  },

  async addNote(id, payload) {
    if (isMockMode) return unwrap(await http.post(`/leads/${id}/notes`, payload))
    return unwrap(await http.post(`/leads/${id}/interacoes`, {
      tipo: payload.tipo || 'ANOTACAO',
      contatoId: payload.contatoId ? Number(payload.contatoId) : null,
      dataHora: payload.dataHora || null,
      descricao: payload.description,
    }))
  },
}
