import { useState } from 'react'
import { Button } from '@/components/common/Button'
import { Field, SelectField, TextArea } from '@/components/common/Field'
import { Modal, SuccessContent } from '@/components/common/Modal'
import { FUNNEL_STAGES } from '@/models/enums'
import styles from './ActionModals.module.css'

export function NewNoteModal({ leadName, saving, error, onClose, onSubmit }) {
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [stage, setStage] = useState('')
  const [done, setDone] = useState(false)

  const submit = async () => {
    const ok = await onSubmit({ title, description, stage })
    if (ok) setDone(true)
  }

  if (done) {
    return (
      <Modal title="" onClose={onClose}>
        <SuccessContent
          title="Anotação registrada"
          description={`O histórico de ${leadName ?? 'contatos'} foi atualizado.`}
          actionLabel="Voltar para empresa"
          onAction={onClose}
        />
      </Modal>
    )
  }

  return (
    <Modal
      title="Nova anotação"
      onClose={onClose}
      footer={
        <>
          <Button onClick={onClose}>Cancelar</Button>
          <Button kind="primary" onClick={submit} loading={saving} disabled={!title.trim()}>
            Adicionar
          </Button>
        </>
      }
    >
      <p className={styles.copy}>
        Registre uma informação importante sobre o relacionamento com a {leadName ?? 'empresa'}.
      </p>
      <div className={styles.form}>
        <Field
          label="Título da anotação"
          placeholder="Ex.: Retorno sobre a proposta comercial"
          value={title}
          onChange={setTitle}
        />
        <TextArea
          label="Descrição da anotação"
          placeholder="Descreva o contexto, decisões e próximos passos..."
          value={description}
          onChange={setDescription}
        />
        <SelectField label="Atualização do funil de vendas" value={stage} onChange={setStage}>
          <option value="">Manter etapa atual</option>
          {FUNNEL_STAGES.map((item) => (
            <option key={item} value={item}>
              {item}
            </option>
          ))}
        </SelectField>
        {error && <p className={styles.error}>{error}</p>}
      </div>
    </Modal>
  )
}

export default NewNoteModal
