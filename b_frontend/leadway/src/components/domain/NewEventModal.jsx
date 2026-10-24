import { useState } from 'react'
import { Button } from '@/components/common/Button'
import { Field, SelectField, TextArea } from '@/components/common/Field'
import { Modal, SuccessContent } from '@/components/common/Modal'
import { APPOINTMENT_TYPES } from '@/models/enums'
import { mockCompanies, mockContacts, mockRepresentatives } from '@/services/mock/fixtures'
import styles from './ActionModals.module.css'

export function NewEventModal({ defaultDate, saving, error, onClose, onSubmit }) {
  const [values, setValues] = useState({
    companyId: '',
    contactId: '',
    date: defaultDate ?? '2025-05-17',
    startTime: '10:00',
    type: 'Reunião',
    representativeId: mockRepresentatives[0]?.id ?? '',
    notes: '',
  })
  const [done, setDone] = useState(false)

  const setField = (field, value) => setValues((prev) => ({ ...prev, [field]: value }))

  const submit = async () => {
    const ok = await onSubmit(values)
    if (ok) setDone(true)
  }

  if (done) {
    return (
      <Modal title="" onClose={onClose}>
        <SuccessContent
          title="Compromisso criado!"
          description="O novo compromisso já aparece na sua agenda."
          actionLabel="Ver agenda"
          onAction={onClose}
        />
      </Modal>
    )
  }

  return (
    <Modal
      title="Novo compromisso"
      onClose={onClose}
      footer={
        <>
          <Button onClick={onClose}>Cancelar</Button>
          <Button kind="primary" onClick={submit} loading={saving} disabled={!values.companyId}>
            Criar compromisso
          </Button>
        </>
      }
    >
      <div className={styles.formGrid}>
        <SelectField label="Empresa" value={values.companyId} onChange={(v) => setField('companyId', v)}>
          <option value="">Selecione uma empresa</option>
          {mockCompanies.map((company) => (
            <option key={company.id} value={company.id}>
              {company.name}
            </option>
          ))}
        </SelectField>
        <SelectField label="Contato" value={values.contactId} onChange={(v) => setField('contactId', v)}>
          <option value="">Selecione um contato</option>
          {mockContacts.map((contact) => (
            <option key={contact.id} value={contact.id}>
              {contact.name}
            </option>
          ))}
        </SelectField>
        <Field label="Data" type="date" value={values.date} onChange={(v) => setField('date', v)} />
        <Field label="Horário" type="time" value={values.startTime} onChange={(v) => setField('startTime', v)} />
        <SelectField label="Tipo" value={values.type} onChange={(v) => setField('type', v)}>
          {APPOINTMENT_TYPES.map((type) => (
            <option key={type} value={type}>
              {type}
            </option>
          ))}
        </SelectField>
        <SelectField
          label="Responsável"
          value={values.representativeId}
          onChange={(v) => setField('representativeId', v)}
        >
          {mockRepresentatives.map((rep) => (
            <option key={rep.id} value={rep.id}>
              {rep.name}
            </option>
          ))}
        </SelectField>
        <TextArea
          className={styles.spanTwo}
          label="Observações"
          placeholder="Adicione contexto para este compromisso..."
          value={values.notes}
          onChange={(v) => setField('notes', v)}
        />
        {error && <p className={[styles.error, styles.spanTwo].join(' ')}>{error}</p>}
      </div>
    </Modal>
  )
}

export default NewEventModal
