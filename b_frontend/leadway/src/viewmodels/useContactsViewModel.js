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
    load()
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
