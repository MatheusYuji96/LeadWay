import { useCallback, useState } from 'react'

/**
 * Base MVVM para operações assíncronas de UI: carregamento, erro e dados.
 * O consumidor controla quando executar via `execute(loader)`.
 *
 * Começa com `loading: true`: até o primeiro resultado (ou erro) chegar, a
 * tela deve mostrar o estado de carregamento — nunca conteúdo sem dados.
 */
export function useAsync() {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const execute = useCallback(async (loader) => {
    setLoading(true)
    setError(null)
    try {
      const result = await loader()
      setData(result)
      return { ok: true, data: result }
    } catch (err) {
      setError(err?.message || 'Não foi possível carregar os dados.')
      return { ok: false, error: err }
    } finally {
      setLoading(false)
    }
  }, [])

  const reset = useCallback(() => {
    setData(null)
    setError(null)
    setLoading(false)
  }, [])

  return { data, setData, loading, error, execute, reset }
}
