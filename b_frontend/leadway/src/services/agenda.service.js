import { http, unwrap, isMockMode } from './http'
import { mockCompanies, mockContacts } from './mock/fixtures'

const asList = (result) => (Array.isArray(result) ? result : result?.items ?? [])

// Os compromissos NÃO ficam no banco do LeadWay: o backend os lê e grava no Cal.com
// com a chave de cada vendedor (por isso quase tudo recebe o vendedorId da sessão).
export const agendaService = {
  /**
   * GET /agenda
   * Se o vendedor ainda não conectou o Cal.com, vem `connected: false` (não é erro).
   * @param {{ month?: string, vendedorId?: number|string }} [params] month no formato yyyy-mm
   * @returns {Promise<{ connected?: boolean, items: import('@/models/appointment').Appointment[], overview: object[] }>}
   */
  async list(params = {}) {
    return unwrap(await http.get('/agenda', { params }))
  },

  /**
   * GET /agenda/slots
   * Horários realmente livres do vendedor no dia (consultados no Cal.com).
   * Cada slot traz `start` (instante completo, é o que volta em `create`) e `label` (HH:mm).
   * @param {{ vendedorId: number|string, date: string }} params date no formato yyyy-mm-dd
   * @returns {Promise<{ date: string, slots: { start: string, label: string }[] }>}
   */
  async slots({ vendedorId, date }) {
    return unwrap(await http.get('/agenda/slots', { params: { vendedorId, date } }))
  },

  /**
   * POST /appointments
   * Cria o compromisso no Cal.com; o Cal.com envia o convite por e-mail ao contato.
   * @param {{ vendedorId: number|string, companyId: number|string, contactId: number|string, slotStart: string, type: string, notes?: string }} payload
   */
  async create(payload) {
    const body = {
      vendedorId: payload.vendedorId,
      leadId: payload.companyId,
      contatoId: payload.contactId,
      slotStart: payload.slotStart,
      tipo: payload.type,
      notas: payload.notes || null,
    }
    return unwrap(await http.post('/appointments', body))
  },

  /**
   * DELETE /appointments/:id
   * Cancela no Cal.com (o contato é avisado por e-mail). `id` é o uid do booking.
   * @param {string} id
   * @param {{ vendedorId: number|string, reason?: string }} options
   */
  async cancel(id, { vendedorId, reason } = {}) {
    return unwrap(await http.delete(`/appointments/${encodeURIComponent(id)}`, {
      params: { vendedorId, motivo: reason },
    }))
  },

  /**
   * PUT /integracoes/calcom
   * O backend valida a API key no Cal.com e a guarda; ela nunca volta para o navegador.
   * @param {{ vendedorId: number|string, apiKey: string }} payload
   */
  async connect({ vendedorId, apiKey }) {
    return unwrap(await http.put('/integracoes/calcom', { vendedorId, apiKey }))
  },

  /** DELETE /integracoes/calcom */
  async disconnect(vendedorId) {
    return unwrap(await http.delete('/integracoes/calcom', { params: { vendedorId } }))
  },

  /**
   * Empresas para o seletor do modal "Novo compromisso" (GET /leads do vendedor).
   * @returns {Promise<{ id: string|number, name: string }[]>}
   */
  async listCompanyOptions(vendedorId) {
    if (isMockMode) return mockCompanies.map((company) => ({ id: company.id, name: company.name }))
    const result = unwrap(await http.get('/leads', { params: { vendedorId } }))
    return asList(result)
      .filter((lead) => lead.ativo !== false)
      .map((lead) => ({ id: lead.id, name: lead.nomeFantasia ?? lead.name ?? 'Sem nome' }))
  },

  /**
   * Contatos de uma empresa (GET /leads/:id/contatos). Cada item devolve o `companyId` para o
   * modal descartar respostas atrasadas de outra empresa.
   * @returns {Promise<{ id: string|number, name: string, email: string, companyId: string|number }[]>}
   */
  async listContactOptions(companyId) {
    if (isMockMode) {
      return mockContacts
        .filter((contact) => String(contact.companyId) === String(companyId))
        .map((contact) => ({ id: contact.id, name: contact.name, email: contact.email, companyId }))
    }
    const result = unwrap(await http.get(`/leads/${companyId}/contatos`))
    return asList(result)
      .filter((contact) => contact.ativo !== false)
      .map((contact) => ({
        id: contact.id, name: contact.nome ?? contact.name ?? 'Sem nome', email: contact.email ?? '', companyId,
      }))
  },
}
