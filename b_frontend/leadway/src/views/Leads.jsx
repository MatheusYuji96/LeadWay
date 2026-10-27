import { useState } from 'react'
import { Button } from '@/components/common/Button'
import { Field, SelectField } from '@/components/common/Field'
import { Icon } from '@/components/common/Icon'
import { PageHeading } from '@/components/common/PagePrimitives'
import { StatCard } from '@/components/common/StatCard'
import { LeadTable } from '@/components/domain/LeadTable'
import { CreateCompanyWizard } from '@/components/domain/CreateCompanyWizard'
import { leadsService } from '@/services'
import { useLeadsViewModel } from '@/viewmodels/useLeadsViewModel'
import { FUNNEL_STAGES, CADENCE_STATUS } from '@/models/enums'
import styles from './Leads.module.css'

export function Leads() {
  const vm = useLeadsViewModel()
  const [wizardOpen, setWizardOpen] = useState(false)
  const [saving, setSaving] = useState(false)
  const [wizardError, setWizardError] = useState(null)

  const createLead = async (values) => {
    setSaving(true)
    setWizardError(null)
    try {
      await leadsService.create(values)
      // Refresh the listing to reflect the real backend state
      await vm.reload()
      window.dispatchEvent(new Event('leadway:leads-changed'))
      return true
    } catch (err) {
      // Propagate the error to the caller so the form can map field errors
      setWizardError(null) // O próprio formulário apresenta a mensagem detalhada
      throw err
    } finally {
      setSaving(false)
    }
  }

  const stats = vm.stats
    ? [
        { key: 'total', icon: 'building', label: 'Total de leads', value: vm.stats.total, detail: 'na sua carteira', tone: 'brand' },
        
      ]
    : []

  return (
    <div className={styles.page}>
      <PageHeading
        eyebrow="Carteira"
        title="Leads"
        description="Todas as empresas da sua carteira, com funil e cadência em dia."
        actions={
          <Button kind="primary" icon="plus" onClick={() => setWizardOpen(true)}>
            Nova empresa
          </Button>
        }
      />

      <div className={styles.stats}>
        {stats.map(({ key, ...props }) => (
  <StatCard key={key} {...props} />
))}
      </div>

      <div className={styles.toolbar}>
        <div className={styles.search}>
          <Field icon="search" placeholder="Buscar por empresa, CNPJ ou representante..." value={vm.searchInput} onChange={vm.changeSearch} />
        </div>
        <SelectField value={vm.stage} onChange={vm.changeStage} className={styles.filter}>
          <option value="">Todas as fases</option>
          {FUNNEL_STAGES.map((stage) => (
            <option key={stage} value={stage}>
              {stage}
            </option>
          ))}
        </SelectField>
        <SelectField value={vm.cadence} onChange={vm.changeCadence} className={styles.filter}>
          <option value="">Qualquer cadência</option>
          {Object.entries(CADENCE_STATUS).map(([key, item]) => (
            <option key={key} value={key}>
              {item.label}
            </option>
          ))}
        </SelectField>
        {vm.hasFilters && (
          <Button kind="ghost" icon="close" onClick={vm.clearFilters}>
            Limpar
          </Button>
        )}
      </div>

      {vm.activeFilters.length > 0 && (
        <div className={styles.chips}>
          {vm.activeFilters.map((chip) => (
            <span key={chip}>{chip}</span>
          ))}
        </div>
      )}

      <div className={styles.tableCard}>
        <LeadTable
          items={vm.items}
          loading={vm.loading}
          error={vm.error}
          onRetry={vm.reload}
          onClearFilters={vm.clearFilters}
        />

        {!vm.loading && !vm.error && vm.total > 0 && (
          <div className={styles.pagination}>
            <span>
              Mostrando <strong>{vm.rangeStart}</strong> — <strong>{vm.rangeEnd}</strong> de{' '}
              <strong>{vm.total}</strong> leads
            </span>
            <div className={styles.pageButtons}>
              <Button size="sm" disabled={vm.page === 1} onClick={() => vm.setPage(vm.page - 1)}>
                Anterior
              </Button>
              <span className={styles.pageNumber}>{vm.page}</span>
              <Button size="sm" disabled={vm.rangeEnd >= vm.total} onClick={() => vm.setPage(vm.page + 1)}>
                Próxima <Icon name="chevron" size={13} />
              </Button>
            </div>
          </div>
        )}
      </div>

      {wizardOpen && (
        <CreateCompanyWizard
          saving={saving}
          error={wizardError}
          onClose={() => setWizardOpen(false)}
          onSubmit={createLead}
        />
      )}
    </div>
  )
}

export default Leads
