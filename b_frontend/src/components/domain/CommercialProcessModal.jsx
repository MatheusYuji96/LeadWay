import { useEffect, useState } from 'react'
import { FUNNEL_STAGES } from '@/models/enums'
import { cadencesService } from '@/services/cadences.service'
import { Button } from '@/components/common/Button'
import styles from './CommercialProcessModal.module.css'

const frequencies = [['DIARIA','Diária'],['SEMANAL','Semanal'],['QUINZENAL','Quinzenal'],['MENSAL','Mensal'],['PERSONALIZADA','Personalizada']]
const statuses = [['ATIVA','Ativa'],['PAUSADA','Pausada'],['CONCLUIDA','Concluída'],['CANCELADA','Cancelada']]
const blank = { contatoId: '', responsavelId: '', frequencia: 'SEMANAL', intervaloDias: '', dataInicio: '', dataFim: '', status: 'ATIVA' }
const asLocal = value => value ? String(value).slice(0,16) : ''
const httpError = error => {
  const status = error?.status ?? error?.httpStatus ?? error?.response?.status
  if (status === 403) return 'HTTP 403 — Sem permissão para cadastrar a cadência deste lead. Verifique o responsável e o perfil do usuário.'
  if (status === 401) return 'HTTP 401 — Sessão expirada ou inválida. Faça login novamente.'
  if (status) return error?.message || `HTTP ${status} — Não foi possível salvar a cadência.`
  return error?.message || 'Não foi possível conectar ao servidor.'
}

export function CommercialProcessModal({ mode = "cadence", lead, leadId, contacts = [], owner, onClose, onStageUpdate, onSaved }) {
  const currentStageId = lead?.etapa?.id ?? lead?.etapaId ?? (FUNNEL_STAGES.indexOf(lead?.stage) + 1)
  const [stageId, setStageId] = useState(String(currentStageId > 0 ? currentStageId : 1))
  const [cadenceId, setCadenceId] = useState(null)
  const [form, setForm] = useState({ ...blank, responsavelId: lead?.representativeId ?? owner?.id ?? '' })
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  useEffect(() => {
    if (mode === "funnel") { setLoading(false); return }
    let active = true
    cadencesService.list().then(result => {
      if (!active) return
      const rows = Array.isArray(result) ? result : result?.items ?? []
      const item = rows.filter(row => String(row.leadId ?? row.lead?.id) === String(leadId)).sort((a,b) => (b.id ?? 0)-(a.id ?? 0))[0]
      if (item) {
        setCadenceId(item.id)
        setForm({ contatoId: item.contatoId ?? '', responsavelId: item.responsavelId ?? lead?.representativeId ?? '', frequencia: item.frequencia ?? 'SEMANAL', intervaloDias: item.intervaloDias ?? '', dataInicio: asLocal(item.dataInicio), dataFim: asLocal(item.dataFim), status: item.status ?? 'ATIVA' })
      }
    }).catch(err => { if(active) setError(httpError(err)) }).finally(() => { if(active) setLoading(false) })
    return () => { active = false }
  }, [leadId, lead?.representativeId, mode])
  const change = (key, value) => setForm(prev => ({ ...prev, [key]: value }))
  const submit = async event => {
    event.preventDefault()
    if (mode === "cadence" && form.frequencia === 'PERSONALIZADA' && (!Number.isInteger(Number(form.intervaloDias)) || Number(form.intervaloDias) < 1)) { setError('Informe um intervalo personalizado maior que zero.'); return }
    if (mode === "cadence" && form.dataInicio && form.dataFim && form.dataFim < form.dataInicio) { setError('A data final não pode ser anterior à inicial.'); return }
    setSaving(true); setError('')
    try {
      if (mode === "funnel") {
        if (String(stageId) !== String(currentStageId)) await onStageUpdate({ etapaId: Number(stageId) })
        await onSaved?.()
        onClose()
        return
      }
      const payload = {
        leadId: Number(leadId),
        contatoId: form.contatoId ? Number(form.contatoId) : null,
        responsavelId: form.responsavelId ? Number(form.responsavelId) : null,
        frequencia: form.frequencia,
        intervaloDias: form.frequencia === 'PERSONALIZADA' ? Number(form.intervaloDias) : null,
        dataInicio: form.dataInicio || null,
        dataFim: form.dataFim || null,
        status: form.status,
      }
      // Não envia propriedades de interface ("em dia", "próxima atividade") que não existem na tabela cadencia.
      if (cadenceId) await cadencesService.update(cadenceId, payload)
      else await cadencesService.create(payload)
      await onSaved?.()
      window.dispatchEvent(new Event('leadway:cadence-updated'))
      onClose()
    } catch (err) { setError(httpError(err)) }
    finally { setSaving(false) }
  }
  return <div className={styles.backdrop} onMouseDown={event => { if(event.target === event.currentTarget) onClose() }}>
    <section className={styles.modal} role="dialog" aria-modal="true" aria-labelledby="commercial-title">
      <header><div><h2 id="commercial-title">{mode === "funnel" ? "Atualizar funil de vendas" : "Atualizar cadência"}</h2><p>{mode === "funnel" ? "Defina a etapa atual desta oportunidade." : "Configure o acompanhamento comercial deste lead."}</p></div><button type="button" onClick={onClose} aria-label="Fechar">×</button></header>
      <form onSubmit={submit}>
        {mode === "funnel" && <div className={styles.section}><h3>↗ Funil de vendas</h3><p>Defina a etapa atual desta oportunidade.</p><label>Etapa do funil<select value={stageId} onChange={e => setStageId(e.target.value)}>{FUNNEL_STAGES.map((name,i) => <option value={i+1} key={name}>{name}</option>)}</select></label></div>}
        {mode === "cadence" && <div className={styles.section}><h3>▣ Cadência</h3><p>Configure o acompanhamento com os campos disponíveis no banco de dados.</p>
          <div className={styles.two}><label>Frequência<select value={form.frequencia} onChange={e => change('frequencia',e.target.value)}>{frequencies.map(([value,label]) => <option key={value} value={value}>{label}</option>)}</select></label><label>Status<select value={form.status} onChange={e => change('status',e.target.value)}>{statuses.map(([value,label]) => <option key={value} value={value}>{label}</option>)}</select></label></div>
          {form.frequencia === 'PERSONALIZADA' && <label>Intervalo em dias<input type="number" min="1" step="1" required value={form.intervaloDias} onChange={e => change('intervaloDias',e.target.value)} /></label>}
          <div className={styles.two}><label>Data de início<input type="datetime-local" value={form.dataInicio} onChange={e => change('dataInicio',e.target.value)} /></label><label>Data de fim<input type="datetime-local" value={form.dataFim} onChange={e => change('dataFim',e.target.value)} /></label></div>
          <label>Contato vinculado (opcional)<select value={form.contatoId} onChange={e => change('contatoId',e.target.value)}><option value="">Sem contato específico</option>{contacts.map(c => <option key={c.id} value={c.id}>{c.name ?? c.nome}</option>)}</select></label>
          <label>Responsável pela cadência<select value={form.responsavelId} onChange={e => change('responsavelId',e.target.value)}><option value="">Responsável atual do Lead</option>{form.responsavelId && <option value={form.responsavelId}>{owner?.name ?? lead?.representativeName ?? 'Responsável atual'}</option>}{!form.responsavelId && lead?.representativeId && <option value={lead.representativeId}>{owner?.name ?? lead?.representativeName}</option>}</select></label>
        </div>}
        {loading && <p>Carregando cadência...</p>}
        {error && <p role="alert" className={styles.error}>{error}</p>}
        <footer><Button type="button" onClick={onClose}>Cancelar</Button><Button kind="primary" type="submit" disabled={saving || loading}>{saving ? 'Salvando...' : mode === 'funnel' ? 'Salvar etapa' : 'Salvar cadência'}</Button></footer>
      </form>
    </section>
  </div>
}
