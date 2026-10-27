import { http, unwrap, isMockMode } from './http'

const digits = (value) => String(value ?? '').replace(/\D/g, '')

export function normalizeLead(raw) {
  const addr = raw.endereco ?? raw.address ?? {}
  const name = raw.name ?? raw.nomeFantasia ?? raw.razaoSocial ?? 'Sem nome'
  const owner = raw.vendedor ?? {}
  const potential = String(raw.potencial ?? '').toUpperCase()
  return {
    ...raw,
    // A resposta atual do backend não expõe o ID interno; CNPJ identifica a linha para navegação.
    id: raw.id ?? raw.cnpj,
    name, cnpj: raw.cnpj ?? '',
    sector: raw.sector ?? raw.setor?.nome ?? '—',
    segment: raw.segment ?? raw.setor?.nome ?? '',
    stage: raw.stage ?? raw.etapa?.nome ?? 'Não definida',
    representativeName: raw.representativeName ?? owner.nome ?? 'Não atribuído',
    representativeId: raw.representativeId ?? owner.id ?? null,
    score: raw.score ?? ({ ALTO: 5, MEDIO: 3, BAIXO: 1 }[potential] ?? 0),
    lastContact: raw.lastContact ?? '—', nextAction: raw.nextAction ?? '—',
    cadenceStatus: raw.cadenceStatus ?? 'ON_TRACK',
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
    // Map frontend filter names to backend query params (Swagger)
    const params = {}
    if (filters.search) params.termo = filters.search
    if (filters.stage) params.fase = filters.stage
    if (filters.vendedorId) params.vendedorId = filters.vendedorId
    // O backend não possui filtro de cadência.
    const result = unwrap(await http.get('/leads', { params }))
    if (isMockMode) return result
    const items = (Array.isArray(result) ? result : result?.items ?? []).map(normalizeLead)
    return { items, total: items.length, stats: { total: items.length } }
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
    return { lead: found, contacts: [], timeline: [], owner: found.vendedor ? { name: found.vendedor.nome, email: found.vendedor.email, phone: found.vendedor.telefone } : null, funnel: null, cadence: null }
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
    if (!isMockMode && (!Number.isSafeInteger(Number(id)) || String(id).length > 10)) {
      throw new Error('O backend retorna o CNPJ, mas a rota PATCH exige o ID numérico do Lead. É necessário expor o ID no LeadResponseDTO para atualizar com segurança.')
    }
    return unwrap(await http.patch(`/leads/${id}`, payload))
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
    if (!isMockMode) throw new Error('Transferência por esta rota não está disponível no backend.')
    return unwrap(await http.post(`/leads/${id}/transfer`, payload))
  },

  /**
   * POST /leads/:id/notes
   * @param {string} id
   * @param {{ title: string, description: string, stage?: string }} payload
   */
  async addNote(id, payload) {
    if (!isMockMode) throw new Error('Anotações por esta rota não estão disponíveis no backend.')
    return unwrap(await http.post(`/leads/${id}/notes`, payload))
  },
}
