import { useEffect, useState } from 'react'
import { cadenceProgress, currentCadence } from '@/utils/cadenceProgress'
import { Panel, PanelHead } from '@/components/common/Panel'
import { Button } from '@/components/common/Button'
import { useLeadCadencesViewModel } from '@/viewmodels/useLeadCadencesViewModel'
import styles from './LeadCadencePanel.module.css'
const blank = { contatoId: '', frequencia: 'SEMANAL', intervaloDias: '', dataInicio: '', dataFim: '', status: 'ATIVA' }
const frequencies = ['DIARIA', 'SEMANAL', 'QUINZENAL', 'MENSAL', 'PERSONALIZADA']
const statuses = ['ATIVA', 'PAUSADA', 'CONCLUIDA', 'CANCELADA']
export function LeadCadencePanel({ leadId, contacts = [], onConfigure }) {
  const vm = useLeadCadencesViewModel(leadId)
  useEffect(() => {
    const refresh = () => vm.reload()
    window.addEventListener('leadway:cadence-updated', refresh)
    return () => window.removeEventListener('leadway:cadence-updated', refresh)
  }, [vm.reload])
  const [now, setNow] = useState(() => new Date())
  useEffect(() => { const timer = setInterval(() => setNow(new Date()), 30000); return () => clearInterval(timer) }, [])
  const selected = currentCadence(vm.items)
  const progress = cadenceProgress(selected, now)
  const [editing, setEditing] = useState(false)
  const [id, setId] = useState(null)
  const [form, setForm] = useState(blank)
  const change = (key, value) => setForm(prev => ({ ...prev, [key]: value }))
  const open = item => { setFormError(''); setId(item?.id ?? null); setForm(item ? { contatoId: item.contatoId ?? '', frequencia: item.frequencia, intervaloDias: item.intervaloDias ?? '', dataInicio: item.dataInicio?.slice(0, 16) ?? '', dataFim: item.dataFim?.slice(0, 16) ?? '', status: item.status } : { ...blank }); setEditing(true) }
  const submit = async event => { event.preventDefault(); if (form.dataInicio && form.dataFim && form.dataFim < form.dataInicio) { setFormError('A data de fim não pode ser anterior à data de início.'); return }; setFormError(''); if (await vm.save(id, form)) setEditing(false) }
  const [formError, setFormError] = useState('')
  return <Panel className={styles.panel}>
    <PanelHead title="Cadência" subtitle="Acompanhamento comercial deste Lead" actions={<Button size="sm" onClick={() => onConfigure ? onConfigure() : open(null)}>Atualizar</Button>} />
    <div className={styles.content}>
      {vm.loading && <p>Carregando cadências...</p>}
      {vm.error && <p role="alert" className={styles.error}>{vm.error}</p>}
      {!vm.loading && vm.items.length === 0 && <p>Nenhuma cadência cadastrada para este Lead.</p>}
      {selected && <div className={styles.overview}>
        <div className={styles.topline}><div className={styles.badges}><span className={`${styles.badge} ${styles[progress.key]}`}>{progress.label}</span><span className={`${styles.badge} ${styles[`STATUS_${selected.status}`] ?? styles.UNTRACKED}`}>{({ ATIVA: "Ativa", PAUSADA: "Pausada", CONCLUIDA: "Concluída", CANCELADA: "Cancelada" })[selected.status] ?? selected.status}</span></div><Button size="sm" onClick={() => open(selected)}>Editar</Button></div>
        <div className={styles.infoLine}><span>Data de registro da cadência</span><strong>{selected.criadoEm ? new Date(selected.criadoEm).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' }) : '—'}</strong></div>
        <div className={styles.track} role="progressbar" aria-valuemin={0} aria-valuemax={100} aria-valuenow={Math.round(progress.progress)} aria-label="Tempo decorrido da cadência"><div className={styles.fill} style={{width: `${progress.progress}%`}} /></div>
        <div className={styles.infoLine}><span>Tempo restante da cadência</span><strong className={styles.remaining}>{progress.remaining}</strong></div>
        <div className={styles.deadline}><strong>DATA LIMITE</strong><p>{progress.end ? progress.end.toLocaleString('pt-BR', {dateStyle:'short',timeStyle:'short'}) : 'Não definida'}</p></div>
        <div className={styles.plan}><strong>Plano de acompanhamento</strong><p>Frequência: {selected.frequencia?.toLowerCase()}</p><p>Situação: {selected.status?.toLowerCase()}</p></div>
      </div>}
      {editing && <form className={styles.form} onSubmit={submit}>
        <h3>{id ? 'Editar cadência' : 'Nova cadência'}</h3>
        <label>Frequência<select value={form.frequencia} onChange={e => change('frequencia', e.target.value)}>{frequencies.map(v => <option key={v} value={v}>{v}</option>)}</select></label>
        {form.frequencia === 'PERSONALIZADA' && <label>Intervalo em dias<input type="number" min="1" required value={form.intervaloDias} onChange={e => change('intervaloDias', e.target.value)} /></label>}
        <label>Contato (opcional)<select value={form.contatoId} onChange={e => change('contatoId', e.target.value)}><option value="">Sem contato específico</option>{contacts.map(c => <option key={c.id} value={c.id}>{c.name ?? c.nome}</option>)}</select></label>
        <label>Data de início<input type="datetime-local" value={form.dataInicio} onChange={e => change('dataInicio', e.target.value)} /></label>
        <label>Data de fim<input type="datetime-local" value={form.dataFim} onChange={e => change('dataFim', e.target.value)} /></label>
        <label>Status<select value={form.status} onChange={e => change('status', e.target.value)}>{statuses.map(v => <option key={v}>{v}</option>)}</select></label>
        {formError && <p className={styles.error} role="alert">{formError}</p>}
        <div className={styles.actions}><Button kind="primary" type="submit" disabled={vm.saving}>{vm.saving ? 'Salvando...' : 'Salvar cadência'}</Button><Button type="button" onClick={() => setEditing(false)}>Cancelar</Button></div>
      </form>}
    </div>
  </Panel>
}
