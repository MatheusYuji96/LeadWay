import { useCallback, useEffect, useMemo, useState } from 'react'
import { representativesService } from '@/services/representatives.service'
import { useAsync } from './useAsync'

/**
 * ViewModel da listagem de funcionários/representantes.
 */
export function useTeamViewModel() {
  const [search, setSearch] = useState('')
  const { data, loading, error, execute } = useAsync()
  const exec = execute

  const load = useCallback(() => exec(() => representativesService.list()), [exec])

  useEffect(() => {
    load()
  }, [load])

  const items = useMemo(() => {
    const all = data?.items ?? []
    const term = search.trim().toLowerCase()
    if (!term) return all
    return all.filter((r) =>
      [r.name, r.code, r.region].some((field) => String(field).toLowerCase().includes(term)),
    )
  }, [data, search])

  return {
    search,
    changeSearch: setSearch,
    representatives: items,
    stats: data?.stats ?? [],
    loading,
    error,
    reload: load,
  }
}
