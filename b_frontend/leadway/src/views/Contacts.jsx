import { useState } from 'react'
import { Button } from '@/components/common/Button'
import { Field, SelectField } from '@/components/common/Field'
import { Modal } from '@/components/common/Modal'
import { PageHeading } from '@/components/common/PagePrimitives'
import { ContactCards } from '@/components/domain/ContactCards'
import { mockCompanies } from '@/services/mock/fixtures'
import { useContactsViewModel } from '@/viewmodels/useContactsViewModel'
import styles from './Contacts.module.css'

export function Contacts() {
  const vm = useContactsViewModel()
  const [newOpen, setNewOpen] = useState(false)
  const [form, setForm] = useState({ name: '', role: '', email: '', whatsapp: '', companyId: mockCompanies[0]?.id ?? '' })
  const [localItems, setLocalItems] = useState(null)

  const setField = (field, value) => setForm((prev) => ({ ...prev, [field]: value }))

  const save = () => {
    if (!form.name.trim()) return
    const company = mockCompanies.find((c) => c.id === form.companyId)
    const created = {
      id: `contact-local-${Date.now()}`,
      name: form.name,
      role: form.role || 'Contato',
      email: form.email,
      whatsapp: form.whatsapp,
      companyId: form.companyId,
      companyName: company?.name ?? '—',
      initials: form.name
        .trim()
        .split(/\s+/)
        .slice(0, 2)
        .map((part) => part[0])
        .join('')
        .toUpperCase(),
      status: 'active',
    }
    setLocalItems((prev) => [created, ...(prev ?? vm.contacts)])
    setNewOpen(false)
    setForm({ name: '', role: '', email: '', whatsapp: '', companyId: mockCompanies[0]?.id ?? '' })
  }

  const base = localItems ?? vm.contacts
  const term = vm.search.trim().toLowerCase()
  const contacts = term
    ? base.filter((c) => [c.name, c.companyName, c.role].some((field) => String(field).toLowerCase().includes(term)))
    : base

  return (
    <div className={styles.page}>
      <PageHeading
        eyebrow="Relacionamentos"
        title="Contatos"
        description="Pessoas das empresas da sua carteira, com status de retorno."
        actions={
          <Button kind="primary" icon="plus" onClick={() => setNewOpen(true)}>
            Novo contato
          </Button>
        }
      />

      <div className={styles.toolbar}>
        <Field
          icon="search"
          placeholder="Buscar por nome, empresa ou cargo..."
          value={vm.search}
          onChange={vm.changeSearch}
        />
        <span className={styles.count}>{contacts.length} contato(s)</span>
      </div>

      <div className={styles.card}>
        <ContactCards contacts={contacts} loading={vm.loading} error={vm.error} onRetry={vm.reload} />
      </div>

      {newOpen && (
        <Modal
          title="Novo contato"
          onClose={() => setNewOpen(false)}
          footer={
            <>
              <Button onClick={() => setNewOpen(false)}>Cancelar</Button>
              <Button kind="primary" onClick={save} disabled={!form.name.trim()}>
                Adicionar contato
              </Button>
            </>
          }
        >
          <div className={styles.formGrid}>
            <Field label="Nome completo" placeholder="Ex.: Ana Souza" value={form.name} onChange={(v) => setField('name', v)} />
            <Field label="Cargo" placeholder="Ex.: Diretora de Compras" value={form.role} onChange={(v) => setField('role', v)} />
            <Field label="E-mail" type="email" placeholder="ana@empresa.com" value={form.email} onChange={(v) => setField('email', v)} />
            <Field label="WhatsApp" placeholder="(11) 99999-0000" value={form.whatsapp} onChange={(v) => setField('whatsapp', v)} />
            <SelectField label="Empresa" value={form.companyId} onChange={(v) => setField('companyId', v)}>
              {mockCompanies.map((company) => (
                <option key={company.id} value={company.id}>
                  {company.name}
                </option>
              ))}
            </SelectField>
          </div>
        </Modal>
      )}
    </div>
  )
}

export default Contacts
