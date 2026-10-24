import { createContext, useContext } from 'react'

/** Contexto da sessão (usuário logado). */
export const SessionContext = createContext(null)

/**
 * Acessa a sessão atual. Precisa estar dentro de <SessionProvider>.
 */
export function useSession() {
  const ctx = useContext(SessionContext)
  if (!ctx) throw new Error('useSession precisa estar dentro de SessionProvider')
  return ctx
}
