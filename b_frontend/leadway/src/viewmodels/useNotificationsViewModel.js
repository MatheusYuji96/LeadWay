import { useCallback, useEffect, useRef, useState } from 'react'
import { notificationsService } from '@/services/notifications.service'
import { useAsync } from './useAsync'

/**
 * ViewModel do sino de notificações do cabeçalho.
 * A primeira carga acontece quando o usuário abre o painel.
 */
export function useNotificationsViewModel() {
  const [open, setOpen] = useState(false)
  const { data, loading, error, execute } = useAsync()
  const exec = execute
  const started = useRef(false)

  const load = useCallback(() => exec(() => notificationsService.list()), [exec])

  useEffect(() => {
    if (open && !started.current) {
      started.current = true
      load()
    }
  }, [open, load])

  const toggle = useCallback(() => setOpen((prev) => !prev), [])
  const close = useCallback(() => setOpen(false), [])

  return {
    open,
    toggle,
    close,
    items: data?.items ?? [],
    unread: data?.unread ?? 0,
    loading,
    error,
    reload: load,
  }
}
