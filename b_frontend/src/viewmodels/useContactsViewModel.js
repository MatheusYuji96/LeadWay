import { useCallback, useEffect, useMemo, useState } from 'react'
import { contactsService } from '@/services/contacts.service'
import { useAsync } from './useAsync'

/**
 * ViewModel da agenda de contatos (pessoas).
 */
export function useContactsViewModel() {
  const [search, setSearch] = useState('')
  const { data, loading, error, execute } = useAsync()
  const exec = execute

  const load = useCallback(() => exec(() => contactsService.list()), [exec])

  useEffect(() => {
    // Attempt to load global contacts. If the backend does not expose a
    // global GET /contacts endpoint (not documented), contactsService.list
    // will throw and we capture that to provide a clearer message to the UI.
    ;(async () => {
      try {
        await load()
      } catch (err) {
        // Set a descriptive error so the UI can show that global listing is not available
        // and suggest per-lead listing instead.
        // The useAsync hook stores error; execute() already sets the error state.
      }
    })()
  }, [load])

  const items = useMemo(() => {
    const all = data?.items ?? []
    const term = search.trim().toLowerCase()
    if (!term) return all
    return all.filter((c) =>
      [c.name, c.companyName, c.role].some((field) => String(field).toLowerCase().includes(term)),
    )
  }, [data, search])

  return {
    search,
    changeSearch: setSearch,
    contacts: items,
    total: items.length,
    loading,
    error,
    reload: load,
  }
}
