import { isMockMode, http, unwrap } from './http'

const SESSION_KEY = 'leadway_session_v1'
const settingsKey = (user) => `leadway_settings_${user?.id ?? user?.email ?? 'guest'}`
const getUser = () => { try { return JSON.parse(localStorage.getItem(SESSION_KEY) || 'null') } catch { return null } }
const read = () => { try { return JSON.parse(localStorage.getItem(settingsKey(getUser())) || '{}') } catch { return {} } }
const write = (patch) => { const next = { ...read(), ...patch }; localStorage.setItem(settingsKey(getUser()), JSON.stringify(next)); return next }
const baseProfile = (user) => ({ name: user?.nome ?? user?.name ?? '', email: user?.email ?? '', whatsapp: user?.telefone ?? user?.whatsapp ?? '', role: '', address: '', cityState: '' })
export const settingsService = {
  async getProfile() {
    if (isMockMode) return unwrap(await http.get('/me/profile'))
    return { ...baseProfile(getUser()), ...(read().profile ?? {}) }
  },
  async updateProfile(payload) {
    if (isMockMode) return unwrap(await http.put('/me/profile', payload))
    write({ profile: payload })
    return payload
  },
  async getPreferences() {
    if (isMockMode) return null
    return read().preferences ?? null
  },
  async updatePreferences(preferences) {
    if (isMockMode) return unwrap(await http.put('/me/preferences', preferences))
    write({ preferences })
    return preferences
  },
  async updatePassword(payload) {
    if (isMockMode) return unwrap(await http.put('/me/password', payload))
    throw new Error('Alteração de senha indisponível: o backend não possui uma rota para trocar a senha. Nenhuma senha foi modificada.')
  },
}
