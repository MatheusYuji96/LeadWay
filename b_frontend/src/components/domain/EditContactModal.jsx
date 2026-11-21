import { useState } from 'react'
import styles from './EditContactModal.module.css'
const digits = (v) => String(v ?? '').replace(/\D/g, '')
export function EditContactModal({ contact, saving, onClose, onSave }) {
  const [form, setForm] = useState({ nome: contact.nome ?? contact.name ?? '', cargo: contact.cargo ?? contact.role ?? '', email: contact.email ?? '', telefone: contact.telefone ?? '', whatsapp: contact.whatsapp ?? '', classificacao: contact.classificacao ?? 'PADRAO' })
  const [error, setError] = useState('')
  const change = (key, value) => setForm((prev) => ({ ...prev, [key]: value }))
  const submit = async (event) => {
    event.preventDefault()
    if (!form.nome.trim() || !form.cargo.trim() || !/^\S+@\S+\.\S+$/.test(form.email) || digits(form.whatsapp).length !== 11) {
      setError('Confira nome, cargo, e-mail e WhatsApp (11 dígitos).'); return
    }
    const ok = await onSave(contact.id, { ...form, telefone: digits(form.telefone), whatsapp: digits(form.whatsapp) })
    if (ok) onClose()
    else setError('Não foi possível atualizar o contato. Verifique o erro exibido na página.')
  }
  return <div className={styles.overlay} role="presentation" onMouseDown={(e) => { if (e.target === e.currentTarget) onClose() }}><form className={styles.modal} onSubmit={submit} aria-label="Editar contato"><header><h2>Editar contato</h2><button type="button" onClick={onClose} aria-label="Fechar">×</button></header><div className={styles.grid}>{[['nome','Nome *'],['cargo','Cargo *'],['email','E-mail *'],['telefone','Telefone'],['whatsapp','WhatsApp *']].map(([key,label]) => <label key={key}>{label}<input value={form[key]} onChange={(e) => change(key,e.target.value)} /></label>)}<label>Classificação<select value={form.classificacao} onChange={(e) => change('classificacao',e.target.value)}><option value="PADRAO">Padrão</option><option value="SECUNDARIO">Secundário</option><option value="PRINCIPAL">Principal</option></select></label></div>{error && <p className={styles.error} role="alert">{error}</p>}<footer><button type="button" onClick={onClose}>Cancelar</button><button type="submit" disabled={saving}>Salvar alterações</button></footer></form></div>
}
