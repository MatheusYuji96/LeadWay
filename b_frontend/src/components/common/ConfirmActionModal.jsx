import { useState } from 'react'
import { Icon } from './Icon'
import styles from './ConfirmActionModal.module.css'

export function ConfirmActionModal({ title = 'Excluir registro?', description, confirmLabel = 'Excluir', onClose, onConfirm }) {
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const confirm = async () => {
    setSaving(true); setError('')
    try { await onConfirm(); onClose() }
    catch (err) { setError(err?.message || 'Não foi possível concluir a operação.') }
    finally { setSaving(false) }
  }
  return <div className={styles.backdrop} onMouseDown={e => { if (e.target === e.currentTarget && !saving) onClose() }}>
    <section className={styles.dialog} role="alertdialog" aria-modal="true" aria-labelledby="confirm-title" aria-describedby="confirm-description">
      <div className={styles.heading}><h2 id="confirm-title">{title}</h2><button type="button" aria-label="Fechar" disabled={saving} onClick={onClose}>×</button></div>
      <p id="confirm-description">{description}</p>
      {error && <p className={styles.error} role="alert">{error}</p>}
      <footer><button type="button" onClick={onClose} disabled={saving}>Cancelar</button><button type="button" className={styles.danger} onClick={confirm} disabled={saving}><Icon name="trash" size={17} />{saving ? 'Excluindo...' : confirmLabel}</button></footer>
    </section>
  </div>
}
