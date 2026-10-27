import { http, unwrap } from './http'
import { isMockMode } from './http'

export const contactsService = {
  /**
   * GET /contacts
   * @returns {Promise<{ items: import('@/models/contact').Contact[] }>}
   */
  async list() {
    if (isMockMode) return unwrap(await http.get('/contacts'))
    // Global listing of contacts is not documented in the Swagger.
    // Use per-lead endpoints: GET /leads/{leadId}/contatos when available.
    throw new Error('GET /contacts não é um endpoint documentado. Use GET /leads/{leadId}/contatos')
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
