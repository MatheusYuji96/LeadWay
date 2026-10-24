import { http, unwrap } from './http'

export const authService = {
  /**
   * POST /auth/login
   * @param {{ email: string, password: string }} credentials
   * @returns {Promise<import('@/models/user').UserSession>}
   */
  async login(credentials) {
    const { user } = unwrap(await http.post('/auth/login', credentials))
    return user
  },

  /**
   * POST /auth/signup
   * @param {{ name: string, whatsapp: string, email: string, password: string }} payload
   * @returns {Promise<import('@/models/user').UserSession>}
   */
  async signup(payload) {
    const { user } = unwrap(await http.post('/auth/signup', payload))
    return user
  },

  async me() {
    const { user } = unwrap(await http.get('/auth/me'))
    return user
  },

  async logout() {
    return unwrap(await http.post('/auth/logout'))
  },
}
