import { useCallback, useEffect, useState } from 'react'
import { leadsService } from '@/services/leads.service'
import { contactsService } from '@/services/contacts.service'
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
        const updated = await leadsService.update(leadId, payload)
        await load()
        return updated ?? { id: leadId }
      } catch (err) {
        setActionError(err?.message || 'Não foi possível salvar as alterações.')
        throw err
      } finally {
        setSaving(false)
      }
    },
    [leadId, load],
  )

  const deleteInteraction = async (interactionId) => {
    setActionError(null)
    setSaving(true)
    try {
      await leadsService.deleteInteraction(leadId, interactionId)
      await load()
      return true
    } catch (err) {
      setActionError(err?.message || 'Não foi possível excluir a interação.')
      return false
    } finally { setSaving(false) }
  }

  const updateContact = async (contactId, payload) => {
    setActionError(null)
    setSaving(true)
    try { await contactsService.update(leadId, contactId, payload); await load(); return true }
    catch (err) { setActionError(err?.message || 'Não foi possível atualizar o contato.'); return false }
    finally { setSaving(false) }
  }
  const deleteContact = async (contactId) => {
    setActionError(null)
    setSaving(true)
    try { await contactsService.remove(leadId, contactId); await load(); return true }
    catch (err) { setActionError(err?.message || 'Não foi possível excluir o contato.'); return false }
    finally { setSaving(false) }
  }

  return {
    updateContact, deleteContact,
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
    deleteInteraction,
    transfer,
    update,
    reload: load,
  }
}
