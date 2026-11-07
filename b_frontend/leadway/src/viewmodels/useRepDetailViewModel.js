import { useCallback, useEffect } from 'react'
import { representativesService } from '@/services/representatives.service'
import { useAsync } from './useAsync'
import { leadsService } from '@/services/leads.service'
import { isMockMode } from '@/services/http'
import { belongsToRepresentative, isProposalStage, isOverdue } from '@/services/representatives.service'

/**
 * ViewModel do detalhe de um representante.
 * @param {string} repId
 */
export function useRepDetailViewModel(repId) {
  const { data, loading, error, execute } = useAsync()
  const exec = execute

  const load = useCallback(() => exec(async () => {
    if (isMockMode) return representativesService.getById(repId)
    const rep = await representativesService.getById(repId)
    const leads = (await leadsService.list()).items.filter((lead) => belongsToRepresentative(lead, rep))
    return { representative: { ...rep, leads: leads.length, completed: leads.filter(isProposalStage).length, pending: leads.filter(isOverdue).length }, leads, stats: [{ key: 'leads', label: 'Leads na carteira', value: leads.length, icon: 'briefcase' }], evolution: [{ month: 'Mai', value: 3 }, { month: 'Jun', value: 5 }, { month: 'Jul', value: 4 }, { month: 'Ago', value: 7 }, { month: 'Set', value: 6 }, { month: 'Out', value: 8 }], notice: null }
  }), [exec, repId])

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
