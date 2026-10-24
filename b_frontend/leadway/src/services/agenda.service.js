import { http, unwrap } from './http'

export const agendaService = {
  /**
   * GET /agenda
   * @param {{ month?: string }} [params] month no formato yyyy-mm
   * @returns {Promise<{ items: import('@/models/appointment').Appointment[], overview: object[], freeSlots: string[] }>}
   */
  async list(params = {}) {
    return unwrap(await http.get('/agenda', { params }))
  },

  /**
   * POST /appointments
   * @param {object} payload
   */
  async create(payload) {
    return unwrap(await http.post('/appointments', payload))
  },
}
