import { useEffect, useState } from 'react'
import { Button } from '@/components/common/Button'
import { Field, SelectField, TextArea } from '@/components/common/Field'
import { Modal, SuccessContent } from '@/components/common/Modal'
import { APPOINTMENT_TYPES } from '@/models/enums'
import { agendaService } from '@/services/agenda.service'
import { useAsync } from '@/viewmodels/useAsync'
import { useSession } from '@/viewmodels/sessionContext'
import styles from './ActionModals.module.css'

export function NewEventModal({ defaultDate, saving, error, onClose, onSubmit }) {
  // O responsável é sempre quem está logado: a reunião vai para o Cal.com DELE.
  const { user } = useSession()
  const vendedorId = user?.id

  const [values, setValues] = useState({
    companyId: '',
    contactId: '',
    date: defaultDate ?? '2025-05-17',
    slotStart: '', // horário livre escolhido (vem do Cal.com)
    type: 'Reunião',
    representativeId: vendedorId ?? '',
    notes: '',
  })
  const [done, setDone] = useState(false)

  const companiesAsync = useAsync()
  const contactsAsync = useAsync()
  const slotsAsync = useAsync()
  const { execute: loadCompanies } = companiesAsync
  const { execute: loadContacts } = contactsAsync
  const { execute: loadSlots } = slotsAsync

  // Empresas reais (antes eram sempre as de teste).
  useEffect(() => {
    loadCompanies(() => agendaService.listCompanyOptions(vendedorId))
  }, [loadCompanies, vendedorId])

  // Sempre que a data muda, busca de novo os horários livres no Cal.com.
  useEffect(() => {
    if (!values.date) return
    loadSlots(() => agendaService.slots({ vendedorId, date: values.date }))
  }, [loadSlots, vendedorId, values.date])

  const setField = (field, value) =>
    setValues((prev) => ({ ...prev, [field]: value, ...(field === 'date' ? { slotStart: '' } : {}) }))

  const changeCompany = (companyId) => {
    setValues((prev) => ({ ...prev, companyId, contactId: '' }))
    if (companyId) loadContacts(() => agendaService.listContactOptions(companyId))
  }

  const companies = companiesAsync.data ?? []
  // Ignora resposta atrasada de outra empresa (o usuário trocou antes de ela chegar).
  const contacts = (contactsAsync.data ?? []).filter((contact) => String(contact.companyId) === String(values.companyId))
  const slotsReady = slotsAsync.data?.date === values.date && !slotsAsync.loading
  const slots = slotsReady ? (slotsAsync.data.slots ?? []) : []

  let slotPlaceholder = 'Selecione um horário'
  if (!slotsReady && !slotsAsync.error) slotPlaceholder = 'Buscando horários...'
  else if (slotsReady && slots.length === 0) slotPlaceholder = 'Sem horários livres neste dia'

  const submit = async () => {
    const ok = await onSubmit({ ...values, vendedorId })
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
          <Button
            kind="primary"
            onClick={submit}
            loading={saving}
            disabled={!values.companyId || !values.contactId || !values.slotStart}
          >
            Criar compromisso
          </Button>
        </>
      }
    >
      <div className={styles.formGrid}>
        <SelectField label="Empresa" value={values.companyId} onChange={changeCompany}>
          <option value="">{companiesAsync.loading ? 'Carregando empresas...' : 'Selecione uma empresa'}</option>
          {companies.map((company) => (
            <option key={company.id} value={company.id}>
              {company.name}
            </option>
          ))}
        </SelectField>
        <SelectField label="Contato" value={values.contactId} onChange={(v) => setField('contactId', v)}>
          <option value="">{values.companyId ? 'Selecione um contato' : 'Escolha a empresa primeiro'}</option>
          {contacts.map((contact) => (
            <option key={contact.id} value={contact.id}>
              {contact.email ? `${contact.name} (${contact.email})` : contact.name}
            </option>
          ))}
        </SelectField>
        <Field label="Data" type="date" value={values.date} onChange={(v) => setField('date', v)} />
        <SelectField label="Horário" value={values.slotStart} onChange={(v) => setField('slotStart', v)}>
          <option value="">{slotPlaceholder}</option>
          {slots.map((slot) => (
            <option key={slot.start} value={slot.start}>
              {slot.label}
            </option>
          ))}
        </SelectField>
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
          <option value={user?.id ?? ''}>{user?.name ?? 'Você'}</option>
        </SelectField>
        <TextArea
          className={styles.spanTwo}
          label="Observações"
          placeholder="Adicione contexto para este compromisso..."
          value={values.notes}
          onChange={(v) => setField('notes', v)}
        />
        {slotsAsync.error && <p className={[styles.error, styles.spanTwo].join(' ')}>{slotsAsync.error}</p>}
        {error && <p className={[styles.error, styles.spanTwo].join(' ')}>{error}</p>}
      </div>
    </Modal>
  )
}

export default NewEventModal
