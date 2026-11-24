import { useState } from 'react'
import { Button } from '@/components/common/Button'
import { Field, SelectField } from '@/components/common/Field'
import { PageHeading } from '@/components/common/PagePrimitives'
import { StatCard } from '@/components/common/StatCard'
import { RepTable } from '@/components/domain/RepTable'
import { CreateRepresentativeWizard } from '@/components/domain/CreateRepresentativeWizard'
import { representativesService } from '@/services'
import { useTeamViewModel } from '@/viewmodels/useTeamViewModel'
import styles from './Team.module.css'

export function Team() {
  const vm = useTeamViewModel()
  const [wizardOpen, setWizardOpen] = useState(false)
  const [status, setStatus] = useState('active')
  const [saving, setSaving] = useState(false)
  const [wizardError, setWizardError] = useState(null)

  const createRep = async (values) => {
    setSaving(true)
    setWizardError(null)
    try {
      await representativesService.create(values)
      await vm.reload()
      return true
    } catch (err) {
      // Prepare a user-facing message based on error type and preserve
      // technical details in the console for diagnosis (but strip sensitive fields).
      const status = err?.status
      let userMessage = 'Não foi possível adicionar o representante.'
      if (status === undefined) {
        userMessage = 'Não foi possível conectar ao servidor. Verifique sua conexão e tente novamente.'
      } else if (status >= 500) {
        userMessage = 'Erro no servidor. Tente novamente mais tarde.'
      } else if (err?.details?.message) {
        userMessage = String(err.details.message)
      } else if (err?.message) {
        userMessage = String(err.message)
      }

      // sanitize details before logging (remove password/senha)
      const sanitize = (obj) => {
        if (!obj || typeof obj !== 'object') return obj
        try {
          const copy = Array.isArray(obj) ? obj.map((i) => sanitize(i)) : Object.fromEntries(
            Object.entries(obj).map(([k, v]) => [k, k === 'senha' || k === 'password' ? '***REDACTED***' : sanitize(v)]),
          )
          return copy
        } catch (e) {
          return obj
        }
      }

      if (err?.details) console.error('Representative creation failed (details):', sanitize(err.details))
      else console.error('Representative creation failed:', err)

      setWizardError(userMessage)
      return false
    } finally {
      setSaving(false)
    }
  }

  const term = vm.search.trim().toLowerCase()
  const base = vm.representatives
  const representatives = base.filter((r) => (status === 'all' || (status === 'active' ? r.active : !r.active)) && (!term || [r.name, r.code, r.region].some((field) => String(field ?? '').toLowerCase().includes(term))))

  return (
    <div className={styles.page}>
      <PageHeading
        eyebrow="Gestão"
        title="Equipe"
        description="Representantes, carteiras e pendências da sua operação."
        actions={
          <Button kind="primary" icon="plus" onClick={() => setWizardOpen(true)}>
            Novo representante
          </Button>
        }
      />

      <div className={styles.stats}>
        {vm.stats.map((stat) => (
          <StatCard key={stat.key} {...stat} />
        ))}
      </div>

      <div className={styles.toolbar}>
        <Field
          icon="search"
          placeholder="Buscar por nome, código ou região..."
          value={vm.search}
          onChange={vm.changeSearch}
        />
        <SelectField value={status} onChange={setStatus} aria-label="Filtrar funcionários por status">
          <option value="active">Ativos</option>
          <option value="inactive">Inativos</option>
          <option value="all">Todos</option>
        </SelectField>
        <span className={styles.count}>{representatives.length} representante(s)</span>
      </div>

      <div className={styles.tableCard}>
        <RepTable
          items={representatives}
          loading={vm.loading}
          error={vm.error}
          onRetry={vm.reload}
          onClearFilters={vm.changeSearch.bind(null, '')}
        />
      </div>

      {!vm.loading && !vm.error && representatives.length > 0 && (
        <p className={styles.note}>
          Clique em um representante para ver carteira, evolução e leads pendentes.
        </p>
      )}

      {wizardOpen && (
        <CreateRepresentativeWizard
          saving={saving}
          error={wizardError}
          onClose={() => setWizardOpen(false)}
          onSubmit={createRep}
        />
      )}
    </div>
  )
}

export default Team
