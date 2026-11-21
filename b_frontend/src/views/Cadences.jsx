import { useState } from 'react'
import { useCadencesViewModel } from '@/viewmodels/useCadencesViewModel'
import styles from './Cadences.module.css'
const initial = {leadId:'', contatoId:'', frequencia:'SEMANAL', intervaloDias:'', dataInicio:'', status:'ATIVA'}
export function Cadences() {
  const vm = useCadencesViewModel()
  const [form, setForm] = useState(initial)
  const [editing, setEditing] = useState(null)
  const set = (key, value) => setForm(prev => ({...prev, [key]:value}))
  const edit = item => { setEditing(item.id); setForm({leadId:String(item.leadId), contatoId:item.contatoId ? String(item.contatoId):'', frequencia:item.frequencia, intervaloDias:item.intervaloDias??'', dataInicio:item.dataInicio?.slice(0,16)??'', status:item.status}) }
  const submit = async event => {
    event.preventDefault()
    const data = {leadId:Number(form.leadId), contatoId:form.contatoId?Number(form.contatoId):null, frequencia:form.frequencia, intervaloDias:form.frequencia==='PERSONALIZADA'?Number(form.intervaloDias):null, dataInicio:form.dataInicio||null, status:form.status}
    if(await vm.save(editing,data)){setEditing(null);setForm(initial)}
  }
  return <main className={styles.page}>
    <h1>Cadências</h1><p>Organize a frequência de acompanhamento dos seus Leads.</p>
    {vm.error && <p role="alert" className={styles.error}>{vm.error}</p>}
    <section className={styles.card}><h2>{editing?'Editar cadência':'Nova cadência'}</h2>
      <form onSubmit={submit} className={styles.form}>
        <label>ID do Lead<input required type="number" min="1" value={form.leadId} onChange={e=>set('leadId',e.target.value)} /></label>
        <label>ID do contato (opcional)<input type="number" min="1" value={form.contatoId} onChange={e=>set('contatoId',e.target.value)} /></label>
        <label>Frequência<select value={form.frequencia} onChange={e=>set('frequencia',e.target.value)}>{['DIARIA','SEMANAL','QUINZENAL','MENSAL','PERSONALIZADA'].map(x=><option key={x}>{x}</option>)}</select></label>
        {form.frequencia==='PERSONALIZADA' && <label>Intervalo em dias<input required type="number" min="1" value={form.intervaloDias} onChange={e=>set('intervaloDias',e.target.value)}/></label>}
        <label>Início<input type="datetime-local" value={form.dataInicio} onChange={e=>set('dataInicio',e.target.value)} /></label>
        <label>Status<select value={form.status} onChange={e=>set('status',e.target.value)}>{['ATIVA','PAUSADA','CONCLUIDA','CANCELADA'].map(x=><option key={x}>{x}</option>)}</select></label>
        <button disabled={vm.saving} type="submit">{vm.saving?'Salvando...':'Salvar cadência'}</button>
        {editing && <button type="button" onClick={()=>{setEditing(null);setForm(initial)}}>Cancelar edição</button>}
      </form>
    </section>
    <section className={styles.card}><h2>Cadências cadastradas</h2>
      {vm.loading?<p>Carregando...</p>:vm.items.length===0?<p>Nenhuma cadência encontrada.</p>:<div className={styles.list}>{vm.items.map(item=><article key={item.id} className={styles.row}><div><strong>Lead #{item.leadId}</strong><p>{item.frequencia} · {item.status} · {item.dataInicio?.replace('T',' ')||'Sem data inicial'}</p></div><button onClick={()=>edit(item)}>Editar</button></article>)}</div>}
    </section>
  </main>
}
