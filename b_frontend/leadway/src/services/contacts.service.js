import { http, unwrap } from './http'
import { isMockMode } from './http'

export const contactsService = {
  /**
   * GET /contacts
   * @returns {Promise<{ items: import('@/models/contact').Contact[] }>}
   */
  async list() {
    if (isMockMode) return unwrap(await http.get('/contacts'))
    const { leadsService } = await import('./leads.service')
    const leads = (await leadsService.list()).items
    const groups = await Promise.allSettled(leads.filter((lead) => lead.id != null).map(async (lead) => {
      const result = await this.listByLead(lead.id)
      const rows = Array.isArray(result) ? result : result?.items ?? []
      return rows.map((item) => ({
        ...item, name: item.name ?? item.nome ?? '', role: item.role ?? item.cargo ?? '',
        companyId: lead.id, companyName: lead.name,
        initials: (item.nome ?? item.name ?? '?').split(' ').slice(0, 2).map((x) => x[0]).join('').toUpperCase(),
        status: item.status ?? 'ON_TRACK',
      }))
    }))
    const rejected = groups.filter((group) => group.status === 'rejected')
    if (rejected.length) throw new Error(`Não foi possível consultar os contatos de ${rejected.length} empresa(s). Confira a API.`)
    return { items: groups.flatMap((group) => group.value) }
  },
  /**
   * GET /leads/:leadId/contatos
   */
  async listByLead(leadId) {
    return unwrap(await http.get(`/leads/${leadId}/contatos`))
  },

  /**
   * POST /leads/:leadId/contatos
   */
  async create(leadId, payload) {
    return unwrap(await http.post(`/leads/${leadId}/contatos`, payload))
  },

  /**
   * PATCH /leads/:leadId/contatos/:id
   */
  async update(leadId, id, payload) {
    return unwrap(await http.patch(`/leads/${leadId}/contatos/${id}`, payload))
  },

  /**
   * DELETE /leads/:leadId/contatos/:id
   */
  async remove(leadId, id) {
    return unwrap(await http.delete(`/leads/${leadId}/contatos/${id}`))
  },

  /**
   * PATCH /leads/:leadId/contatos/:contatoId/mudar-lead/:novoLeadId
   */
  async changeLead(leadId, contatoId, novoLeadId) {
    return unwrap(await http.patch(`/leads/${leadId}/contatos/${contatoId}/mudar-lead/${novoLeadId}`))
  },
}
