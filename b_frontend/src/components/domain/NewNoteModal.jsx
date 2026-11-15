import { useState } from 'react'
import { Button } from '@/components/common/Button'
import { Field, SelectField, TextArea } from '@/components/common/Field'
import { Modal, SuccessContent } from '@/components/common/Modal'
import styles from './ActionModals.module.css'

const TYPES = [['ANOTACAO', 'Anotação'], ['LIGACAO', 'Ligação'], ['REUNIAO', 'Reunião'], ['VISITA', 'Visita'], ['EMAIL', 'E-mail'], ['WHATSAPP', 'WhatsApp']]

export function NewNoteModal({ leadName, contacts = [], saving, error, onClose, onSubmit }) {
  const [tipo, setTipo] = useState('ANOTACAO')
  const [contatoId, setContatoId] = useState('')
  const [dataHora, setDataHora] = useState(() => {
    const d = new Date()
    return new Date(d.getTime() - d.getTimezoneOffset() * 60000).toISOString().slice(0, 16)
  })
  const [description, setDescription] = useState('')
  const [done, setDone] = useState(false)

  const submit = async () => {
    if (!description.trim()) return
    const ok = await onSubmit({ tipo, contatoId: contatoId || null, dataHora: dataHora || null, description: description.trim() })
    if (ok) setDone(true)
  }

  if (done) return (
    <Modal title="" onClose={onClose}>
      <SuccessContent title="Interação registrada com sucesso!"
        description={`O histórico de ${leadName ?? 'contatos'} foi atualizado.`}
        actionLabel="Voltar para empresa" onAction={onClose} />
    </Modal>
  )

  return (
    <Modal title="Registrar interação" onClose={onClose} footer={<>
      <Button onClick={onClose}>Cancelar</Button>
      <Button kind="primary" onClick={submit} loading={saving} disabled={!description.trim() || saving}>Salvar interação</Button>
    </>}>
      <p className={styles.copy}>Registre uma atividade realizada com {leadName ?? 'a empresa'}.</p>
      <div className={styles.form}>
        <SelectField label="Tipo de interação" value={tipo} onChange={setTipo}>
          {TYPES.map(([value, label]) => <option key={value} value={value}>{label}</option>)}
        </SelectField>
        <SelectField label="Contato vinculado (opcional)" value={contatoId} onChange={setContatoId}>
          <option value="">Interação geral com a empresa</option>
          {contacts.map((c) => <option key={c.id} value={c.id}>{c.name ?? c.nome}</option>)}
        </SelectField>
        <Field label="Data e hora" type="datetime-local" value={dataHora} onChange={setDataHora} />
        <TextArea label="Descrição *" placeholder="O que aconteceu? Quais foram os próximos passos?"
          value={description} onChange={setDescription} />
        {error && <p className={styles.error}>{error}</p>}
      </div>
    </Modal>
  )
}

export default NewNoteModal
