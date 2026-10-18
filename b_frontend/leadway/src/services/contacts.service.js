import { http, unwrap } from './http'

export const contactsService = {
  /**
   * GET /contacts
   * @returns {Promise<{ items: import('@/models/contact').Contact[] }>}
   */
  async list() {
    return unwrap(await http.get('/contacts'))
  },
}
