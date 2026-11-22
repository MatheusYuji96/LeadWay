import { useCallback, useEffect, useState } from 'react'
import { cadencesService } from '@/services/cadences.service'

export function useLeadCadencesViewModel(leadId) {
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const reload = useCallback(async () => {
    setLoading(true); setError('')
    try {
      const rows = await cadencesService.list()
      setItems((Array.isArray(rows) ? rows : rows?.items ?? []).filter(c => String(c.leadId) === String(leadId)))
    } catch (err) { setError(err.message || 'Não foi possível carregar as cadências.') }
    finally { setLoading(false) }
  }, [leadId])
  useEffect(() => { reload() }, [reload])
  const save = async (id, form) => {
    setSaving(true); setError('')
    try {
      const data = { ...form, leadId: Number(leadId), contatoId: form.contatoId ? Number(form.contatoId) : null,
        intervaloDias: form.frequencia === 'PERSONALIZADA' ? Number(form.intervaloDias) : null,
        dataInicio: form.dataInicio || null, dataFim: form.dataFim || null, responsavelId: form.responsavelId ? Number(form.responsavelId) : null }
      if (id) await cadencesService.update(id, data)
      else await cadencesService.create(data)
      await reload(); return true
    } catch (err) { setError(err.message || 'Erro ao salvar cadência.'); return false }
    finally { setSaving(false) }
  }
  return { items, loading, saving, error, reload, save }
}
