import { useCallback, useEffect, useState } from 'react'
import { cadencesService } from '@/services/cadences.service'
export function useCadencesViewModel() {
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const reload = useCallback(async () => {
    setLoading(true); setError('')
    try { setItems(await cadencesService.list()) } catch(e) { setError(e.message) }
    finally { setLoading(false) }
  }, [])
  useEffect(() => { reload() }, [reload])
  const save = async (id, data) => {
    setSaving(true); setError('')
    try { if (id) await cadencesService.update(id, data); else await cadencesService.create(data); await reload(); return true }
    catch(e) { setError(e.message); return false } finally { setSaving(false) }
  }
  return {items, loading, saving, error, reload, save}
}
