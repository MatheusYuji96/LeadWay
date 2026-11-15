import { http, unwrap, isMockMode } from './http'

export const authService = {
  /**
   * POST /auth/login
   * @param {{ email: string, password: string }} credentials
   * @returns {Promise<import('@/models/user').UserSession>}
   */
  async login(credentials) {
    // The backend expects { email, senha }
    const payload = { email: credentials.email, senha: credentials.password }
    const res = unwrap(await http.post('/auth/login', payload))
    // In mock mode the adapter returns { user }, while the real API returns the user object directly.
    if (isMockMode && res && res.user) return res.user
    return res
  },

  /**
   * POST /auth/signup
   * @param {{ name: string, whatsapp: string, email: string, password: string }} payload
   * @returns {Promise<import('@/models/user').UserSession>}
   */
  async signup(payload) {
    // Map frontend fields to API contract (use documentation as source of truth)
    // API expects: { nome, email, senha, confirmarSenha, telefone }
    const telefone = String(payload.whatsapp || payload.telefone || '').replace(/\D/g, '')
    const body = {
      nome: payload.name || payload.nome || '',
      email: payload.email,
      senha: payload.password || payload.senha,
      confirmarSenha: payload.confirmPassword || payload.confirmarSenha || payload.password || payload.senha,
      telefone,
    }
    const signupPath = isMockMode ? '/auth/signup' : '/auth'
    const res = unwrap(await http.post(signupPath, body))
    if (isMockMode && res && res.user) return res.user
    return res
  },

  async me() {
    const { user } = unwrap(await http.get('/auth/me'))
    return user
  },

  async logout() {
    return unwrap(await http.post('/auth/logout'))
  },
}
