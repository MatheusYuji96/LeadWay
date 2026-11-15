import { useCallback, useEffect, useRef, useState } from 'react'
import { dashboardService } from '@/services/dashboard.service'
import { useAsync } from './useAsync'
import { useSession } from './sessionContext'

/**
 * ViewModel do dashboard (abas "Minha visão" e "Acompanhamento da equipe").
 */
export function useDashboardViewModel() {
  const { user } = useSession()
  const vendedorId = user?.id ?? user?.vendedorId ?? user?.vendedor?.id
  const [tab, setTab] = useState('overview')
  const overview = useAsync()
  const team = useAsync()
  const execOverview = overview.execute
  const execTeam = team.execute
  const teamStarted = useRef(false)

  const loadOverview = useCallback(() => execOverview(() => dashboardService.getOverview(vendedorId)), [execOverview, vendedorId])
  const loadTeam = useCallback(() => execTeam(() => dashboardService.getTeamOverview()), [execTeam])

  useEffect(() => {
    loadOverview()
  }, [loadOverview])

  useEffect(() => {
    if (tab === 'team' && !teamStarted.current) {
      teamStarted.current = true
      loadTeam()
    }
  }, [loadTeam, tab])

  return {
    tab,
    setTab,
    overview: overview.data,
    overviewLoading: overview.loading,
    overviewError: overview.error,
    reloadOverview: loadOverview,
    team: team.data,
    teamLoading: team.loading,
    teamError: team.error,
    reloadTeam: loadTeam,
  }
}
