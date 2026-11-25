import { isMockMode, http, unwrap } from './http'

const SESSION_KEY = 'leadway_session_v1'
const settingsKey = (user) => `leadway_settings_${user?.id ?? user?.email ?? 'guest'}`
const getUser = () => { try { return JSON.parse(localStorage.getItem(SESSION_KEY) || 'null') } catch { return null } }
const read = () => { try { return JSON.parse(localStorage.getItem(settingsKey(getUser())) || '{}') } catch { return {} } }
const write = (patch) => { const next = { ...read(), ...patch }; localStorage.setItem(settingsKey(getUser()), JSON.stringify(next)); return next }
const roleLabel = (user) => {
  const role = user?.cargo?.nome ?? user?.cargoNome ?? user?.cargo ?? user?.role ?? user?.perfil ?? ''
  if (typeof role !== 'string') return ''
  const names = { ADMIN: 'Administrador', ADMINISTRADOR: 'Administrador', GERENTE: 'Gerente', VENDEDOR: 'Vendedor', GESTOR: 'Gerente' }
  return names[role.toUpperCase()] ?? role
}
const baseProfile = (user) => ({ name: user?.nome ?? user?.name ?? '', email: user?.email ?? '', whatsapp: user?.telefone ?? user?.whatsapp ?? '', role: roleLabel(user) })
export const settingsService = {
  async getProfile() {
    if (isMockMode) return unwrap(await http.get('/me/profile'))
    const user = unwrap(await http.get('/auth/me'))
    return baseProfile(user)
  },
  async updateProfile(payload) {
    if (isMockMode) return unwrap(await http.put('/me/profile', payload))
    const updated = unwrap(await http.patch('/auth/me', { nome: payload.name, email: payload.email, telefone: (payload.whatsapp || '').replace(/\D/g, '') }))
    return baseProfile(updated)
  },
  async getPreferences() {
    if (isMockMode) return null
    // Preferências locais: a tabela preferencias_vendedor foi retirada da modelagem.
    return read().preferences ?? null
  },
  async updatePreferences(preferences) {
    if (isMockMode) return unwrap(await http.put('/me/preferences', preferences))
    write({ preferences })
    return preferences
  },
  async updatePassword(payload) {
    if (isMockMode) return unwrap(await http.put('/me/password', payload))
    return unwrap(await http.put('/auth/me/password', payload))
  },
}
