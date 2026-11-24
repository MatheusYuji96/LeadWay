import { useCallback, useEffect, useMemo, useState } from 'react'
import { leadsService } from '@/services/leads.service'
import { useAsync } from './useAsync'
import { CADENCE_STATUS } from '@/models/enums'

/**
 * ViewModel da listagem de leads: filtros, busca, estatísticas e paginação.
 */
export function useLeadsViewModel() {
  const [searchInput, setSearchInput] = useState('')
  const [stage, setStage] = useState('')
  const [cadence, setCadence] = useState('')
  const [owner, setOwner] = useState('')
  const [status, setStatus] = useState('active')
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

  const changeOwner = useCallback((value) => { setOwner(value); setPage(1) }, [])

  const clearFilters = useCallback(() => {
    setSearchInput('')
    setStage('')
    setCadence('')
    setOwner('')
    setStatus('active')
    setPage(1)
  }, [])

  const items = useMemo(() => data?.items ?? [], [data])
  const stats = data?.stats ?? null
  const owners = useMemo(() => Array.from(new Map(items.filter((item) => item.representativeId != null).map((item) => [String(item.representativeId), { id: String(item.representativeId), name: item.representativeName }])).values()).sort((a, b) => a.name.localeCompare(b.name)), [items])

  const filteredItems = useMemo(() => items.filter((item) => (status === 'all' || (status === 'active' ? item.ativo !== false : item.ativo === false)) && (!owner || String(item.representativeId ?? 'unassigned') === owner)), [items, owner, status])

  const visibleItems = useMemo(() => {
    const start = (page - 1) * pageSize
    return filteredItems.slice(start, start + pageSize)
  }, [filteredItems, page, pageSize])

  const rangeStart = filteredItems.length === 0 ? 0 : (page - 1) * pageSize + 1
  const rangeEnd = Math.min(page * pageSize, filteredItems.length)

  const activeFilters = [status !== 'active' && `Status: ${status === 'inactive' ? 'Inativo' : 'Todos'}`, stage && `Fase: ${stage}`, cadence && `Cadência: ${CADENCE_STATUS[cadence]?.label ?? cadence}`, owner && `Responsável: ${owner === 'unassigned' ? 'Não atribuído' : owners.find((item) => item.id === owner)?.name ?? owner}`].filter(Boolean)

  return {
    searchInput,
    stage,
    cadence,
    owner,
    status,
    changeStatus: (value) => { setStatus(value); setPage(1) },
    owners,
    page,
    pageSize,
    items: visibleItems,
    total: filteredItems.length,
    rangeStart,
    rangeEnd,
    stats,
    loading,
    error,
    activeFilters,
    hasFilters: Boolean(searchInput || stage || cadence || owner),
    changeSearch,
    changeStage,
    changeCadence,
    changeOwner,
    clearFilters,
    setPage,
    reload: load,
  }
}
