import { useCallback, useEffect } from 'react'
import { representativesService } from '@/services/representatives.service'
import { useAsync } from './useAsync'

/**
 * ViewModel do detalhe de um representante.
 * @param {string} repId
 */
export function useRepDetailViewModel(repId) {
  const { data, loading, error, execute } = useAsync()
  const exec = execute

  const load = useCallback(() => exec(() => representativesService.getById(repId)), [exec, repId])

  useEffect(() => {
    load()
  }, [load])

  return {
    representative: data?.representative ?? null,
    notice: data?.notice ?? null,
    stats: data?.stats ?? [],
    evolution: data?.evolution ?? [],
    leads: data?.leads ?? [],
    loading,
    error: error || (data && !data.representative ? 'Representante não encontrado.' : null),
    reload: load,
  }
}
