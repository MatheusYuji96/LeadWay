import { useCallback, useEffect, useMemo, useState } from 'react'
import { leadsService } from '@/services/leads.service'
import { useAsync } from './useAsync'

/**
 * ViewModel da listagem de leads: filtros, busca, estatísticas e paginação.
 */
export function useLeadsViewModel() {
  const [searchInput, setSearchInput] = useState('')
  const [stage, setStage] = useState('')
  const [cadence, setCadence] = useState('')
  const [page, setPage] = useState(1)
  const { data, loading, error, execute } = useAsync()

  const pageSize = 10

  const load = useCallback(
    (overrides = {}) =>
      execute(() =>
        leadsService.list({
          search: overrides.search ?? searchInput,
          stage: overrides.stage ?? stage,
          cadence: overrides.cadence ?? cadence,
        }),
      ),
    [cadence, execute, searchInput, stage],
  )

  useEffect(() => {
    const timer = setTimeout(() => load(), 300)
    return () => clearTimeout(timer)
  }, [load])

  const changeSearch = useCallback((value) => {
    setSearchInput(value)
    setPage(1)
  }, [])

  const changeStage = useCallback((value) => {
    setStage(value)
    setPage(1)
  }, [])

  const changeCadence = useCallback((value) => {
    setCadence(value)
    setPage(1)
  }, [])

  const clearFilters = useCallback(() => {
    setSearchInput('')
    setStage('')
    setCadence('')
    setPage(1)
  }, [])

  const items = useMemo(() => data?.items ?? [], [data])
  const stats = data?.stats ?? null

  const visibleItems = useMemo(() => {
    const start = (page - 1) * pageSize
    return items.slice(start, start + pageSize)
  }, [items, page, pageSize])

  const rangeStart = items.length === 0 ? 0 : (page - 1) * pageSize + 1
  const rangeEnd = Math.min(page * pageSize, items.length)

  const activeFilters = [stage && `Fase: ${stage}`, cadence && 'Cadência: personalizada'].filter(Boolean)

  return {
    searchInput,
    stage,
    cadence,
    page,
    pageSize,
    items: visibleItems,
    total: items.length,
    rangeStart,
    rangeEnd,
    stats,
    loading,
    error,
    activeFilters,
    hasFilters: Boolean(searchInput || stage || cadence),
    changeSearch,
    changeStage,
    changeCadence,
    clearFilters,
    setPage,
    reload: () => load(),
  }
}
