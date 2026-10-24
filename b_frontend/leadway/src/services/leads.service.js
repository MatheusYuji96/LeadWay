import { http, unwrap } from './http'

export const leadsService = {
  /**
   * GET /leads
   * @param {{ search?: string, stage?: string, cadence?: string }} [filters]
   * @returns {Promise<{ items: import('@/models/lead').Lead[], total: number, stats: object }>}
   */
  async list(filters = {}) {
    const params = Object.fromEntries(Object.entries(filters).filter(([, v]) => v !== '' && v != null))
    return unwrap(await http.get('/leads', { params }))
  },

  /**
   * GET /leads/:id
   * Detalhe completo: lead, contatos, timeline, responsável, funil e cadência.
   * @param {string} id
   */
  async getById(id) {
    return unwrap(await http.get(`/leads/${id}`))
  },

  /**
   * POST /leads
   * @param {object} payload
   */
  async create(payload) {
    return unwrap(await http.post('/leads', payload))
  },

  /**
   * PUT /leads/:id
   * @param {string} id
   * @param {object} payload
   */
  async update(id, payload) {
    return unwrap(await http.put(`/leads/${id}`, payload))
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
    return unwrap(await http.post(`/leads/${id}/transfer`, payload))
  },

  /**
   * POST /leads/:id/notes
   * @param {string} id
   * @param {{ title: string, description: string, stage?: string }} payload
   */
  async addNote(id, payload) {
    return unwrap(await http.post(`/leads/${id}/notes`, payload))
  },
}
