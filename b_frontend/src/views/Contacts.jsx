import { useState, useEffect } from 'react'
import { useSearchParams } from 'react-router-dom'
import { Button } from '@/components/common/Button'
import { Field, SelectField } from '@/components/common/Field'
import { Modal } from '@/components/common/Modal'
import { PageHeading } from '@/components/common/PagePrimitives'
import { ContactCards } from '@/components/domain/ContactCards'
import { leadsService } from '@/services/leads.service'
import { contactsService } from '@/services/contacts.service'
import { useContactsViewModel } from '@/viewmodels/useContactsViewModel'
import styles from './Contacts.module.css'

export function Contacts() {
  const vm = useContactsViewModel()
  const [searchParams] = useSearchParams()
  const leadFilter = searchParams.get('leadId')
  const [newOpen, setNewOpen] = useState(false)
  const [form, setForm] = useState({ name: '', role: '', email: '', whatsapp: '', companyId: '' })
  const [localItems, setLocalItems] = useState(null)
  const [companies, setCompanies] = useState([])
  const [saving, setSaving] = useState(false)

  const setField = (field, value) => setForm((prev) => ({ ...prev, [field]: value }))

  const save = async () => {
    if (!form.name.trim()) return
    setSaving(true)
    try {
      // Prepare payload according to API: { nome, cargo, email, whatsapp, leadId, contato, ativo }
      // client-side validation
      if (!form.whatsapp || !/^[0-9]{11}$/.test(String(form.whatsapp).replace(/\D/g, ''))) {
        // simple inline feedback: keep modal open and log error
        console.error('WhatsApp deve ter 11 dígitos (somente números)')
        setSaving(false)
        return
      }
      const payload = {
        nome: form.name,
        cargo: form.role,
        email: form.email,
        whatsapp: String(form.whatsapp || '').replace(/\D/g, ''),
        leadId: form.companyId,
        contato: 'PADRAO',
        ativo: 1,
      }
      await contactsService.create(form.companyId, payload)
      await vm.reload()
      setNewOpen(false)
      setForm({ name: '', role: '', email: '', whatsapp: '', companyId: companies[0]?.id ?? '' })
    } catch (err) {
      // show error via vm.reload side-effects or keep modal open
      // For now, surface error by leaving modal open and logging
      console.error(err)
    } finally {
      setSaving(false)
    }
  }

  const base = localItems ?? vm.contacts
  const term = vm.search.trim().toLowerCase()
  const filteredByLead = leadFilter ? base.filter((c) => String(c.companyId) === String(leadFilter)) : base
  const contacts = term
    ? filteredByLead.filter((c) => [c.name, c.companyName, c.role].some((field) => String(field).toLowerCase().includes(term)))
    : filteredByLead

  // Load companies for the "Empresa" select
  useEffect(() => {
    let mounted = true
    ;(async () => {
      try {
        const res = await leadsService.list()
        const items = res?.items ?? []
        if (mounted) setCompanies(items)
        // initialize default company if not set
        if (mounted && !form.companyId && items[0]) setForm((f) => ({ ...f, companyId: items[0].id }))
      } catch (e) {
        // ignore
      }
    })()
    return () => (mounted = false)
  }, [])

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
              {companies.map((company) => (
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
