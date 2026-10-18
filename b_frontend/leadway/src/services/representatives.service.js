import { http, unwrap } from './http'

export const representativesService = {
  /**
   * GET /representatives
   * @returns {Promise<{ items: import('@/models/representative').Representative[], stats: object[] }>}
   */
  async list() {
    return unwrap(await http.get('/representatives'))
  },

  /**
   * GET /representatives/:id
   * @param {string} id
   */
  async getById(id) {
    return unwrap(await http.get(`/representatives/${id}`))
  },

  /**
   * POST /representatives
   * @param {object} payload
   */
  async create(payload) {
    return unwrap(await http.post('/representatives', payload))
  },
}
