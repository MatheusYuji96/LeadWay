import { useCallback, useEffect, useMemo, useState } from 'react'
import { authService } from '@/services/auth.service'
import { SessionContext } from './sessionContext'

const SESSION_KEY = 'leadway_session_v1'

function normalizeUser(value) {
  if (!value) return null
  const name = value.name ?? value.nome ?? value.user?.name ?? value.user?.nome ?? ''
  return { ...value, name: name || value.email || 'Usuário', initials: name.trim().split(/\s+/).slice(0, 2).map((word) => word[0]).join('').toUpperCase(), role: value.role ?? 'Usuário' }
}

/**
 * Sessão da aplicação com persistência em localStorage.
 */
export function SessionProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      const raw = localStorage.getItem(SESSION_KEY)
      if (raw) {
        return normalizeUser(JSON.parse(raw))
      }
    } catch (e) {
      // ignore
    }
    return null
  })

  useEffect(() => {
    try {
      if (user) {
        localStorage.setItem(SESSION_KEY, JSON.stringify(user))
      } else {
        localStorage.removeItem(SESSION_KEY)
      }
    } catch (e) {
      // ignore
    }
  }, [user])

  const login = useCallback(async (credentials) => {
    const logged = await authService.login(credentials)
    setUser(normalizeUser(logged))
    return logged
  }, [])

  const signup = useCallback(async (payload) => {
    const created = await authService.signup(payload)
    setUser(normalizeUser(created))
    return created
  }, [])

  const logout = useCallback(async () => {
    try {
      await authService.logout()
    } catch (e) {
      // ignore
    }
    setUser(null)
  }, [])

  const value = useMemo(() => ({ user, login, signup, logout }), [user, login, signup, logout])

  return <SessionContext.Provider value={value}>{children}</SessionContext.Provider>
}
