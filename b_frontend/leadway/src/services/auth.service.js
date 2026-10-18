import { http, unwrap } from './http'

/**
 * Autenticação / sessão.
 *
 * Contrato pendente no backend — ver docs/API_CONTRACTS.md.
 * O fluxo de UI não inventa armazenamento de token: enquanto o backend
 * não define o mecanismo, a sessão vive apenas no estado da aplicação.
 */
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
}
