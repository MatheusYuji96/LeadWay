import { useCallback, useMemo, useState } from 'react'
import { isMockMode } from '@/services'
import { authService } from '@/services/auth.service'
import { mockUser } from '@/services/mock/fixtures'
import { SessionContext } from './sessionContext'

/**
 * Sessão da aplicação.
 *
 * O mecanismo real de autenticação (token/cookie) ainda não está definido
 * com o backend — ver docs/API_CONTRACTS.md. Enquanto isso, a sessão vive
 * apenas em memória: o login/signup grava o usuário retornado pelo serviço
 * e o logout limpa o estado.
 */
export function SessionProvider({ children }) {
  const [user, setUser] = useState(() => (isMockMode ? mockUser : null))

  const login = useCallback(async (credentials) => {
    const logged = await authService.login(credentials)
    setUser(logged)
    return logged
  }, [])

  const signup = useCallback(async (payload) => {
    const created = await authService.signup(payload)
    setUser(created)
    return created
  }, [])

  const logout = useCallback(() => setUser(null), [])

  const value = useMemo(() => ({ user, login, signup, logout }), [user, login, signup, logout])

  return <SessionContext.Provider value={value}>{children}</SessionContext.Provider>
}
