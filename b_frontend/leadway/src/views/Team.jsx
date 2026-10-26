import { useState } from 'react'
import { Button } from '@/components/common/Button'
import { Field } from '@/components/common/Field'
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
  const [saving, setSaving] = useState(false)
  const [wizardError, setWizardError] = useState(null)
  const [extraReps, setExtraReps] = useState([])

  const createRep = async (values) => {
    setSaving(true)
    setWizardError(null)
    try {
      await representativesService.create(values)
      setExtraReps((prev) => [
        {
          id: `rep-local-${Date.now()}`,
          name: values.name,
          code: values.code || 'SEM-CÓD',
          region: values.region || '—',
          email: values.email,
          phone: values.phone,
          initials: values.name
            .trim()
            .split(/\s+/)
            .slice(0, 2)
            .map((part) => part[0])
            .join('')
            .toUpperCase(),
          leads: 0,
          contacts: 0,
          pending: 0,
          active: values.active,
        },
        ...prev,
      ])
      return true
    } catch (err) {
      setWizardError(err?.message || 'Não foi possível adicionar o representante.')
      return false
    } finally {
      setSaving(false)
    }
  }

  const term = vm.search.trim().toLowerCase()
  const base = [...extraReps, ...vm.representatives]
  const representatives = term
    ? base.filter((r) => [r.name, r.code, r.region].some((field) => String(field).toLowerCase().includes(term)))
    : base

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
