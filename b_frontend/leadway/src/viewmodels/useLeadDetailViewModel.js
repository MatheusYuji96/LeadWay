import { useCallback, useEffect, useState } from 'react'
import { leadsService } from '@/services/leads.service'
import { useAsync } from './useAsync'

/**
 * ViewModel do detalhe de um lead (empresa), incluindo transferência,
 * edição e novas anotações.
 * @param {string} leadId
 */
export function useLeadDetailViewModel(leadId) {
  const { data, loading, error, execute } = useAsync()
  const exec = execute
  const [actionError, setActionError] = useState(null)
  const [saving, setSaving] = useState(false)

  const load = useCallback(() => exec(() => leadsService.getById(leadId)), [exec, leadId])

  useEffect(() => {
    load()
  }, [load])

  const addNote = useCallback(
    async (payload) => {
      setActionError(null)
      setSaving(true)
      try {
        await leadsService.addNote(leadId, payload)
        await load()
        return true
      } catch (err) {
        setActionError(err?.message || 'Não foi possível salvar a anotação.')
        return false
      } finally {
        setSaving(false)
      }
    },
    [leadId, load],
  )

  const transfer = useCallback(
    async (payload) => {
      setActionError(null)
      setSaving(true)
      try {
        await leadsService.transfer(leadId, payload)
        await load()
        return true
      } catch (err) {
        setActionError(err?.message || 'Não foi possível transferir o responsável.')
        return false
      } finally {
        setSaving(false)
      }
    },
    [leadId, load],
  )

  const update = useCallback(
    async (payload) => {
      setActionError(null)
      setSaving(true)
      try {
        await leadsService.update(leadId, payload)
        await load()
        return true
      } catch (err) {
        setActionError(err?.message || 'Não foi possível salvar as alterações.')
        return false
      } finally {
        setSaving(false)
      }
    },
    [leadId, load],
  )

  return {
    lead: data?.lead ?? null,
    contacts: data?.contacts ?? [],
    timeline: data?.timeline ?? [],
    owner: data?.owner ?? null,
    funnel: data?.funnel ?? null,
    cadence: data?.cadence ?? null,
    loading,
    error: error || (data && !data.lead ? 'Empresa não encontrada.' : null),
    actionError,
    saving,
    addNote,
    transfer,
    update,
    reload: load,
  }
}
