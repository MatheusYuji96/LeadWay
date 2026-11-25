import { useEffect, useState } from 'react'
import { Button } from '@/components/common/Button'
import { Field, SelectField } from '@/components/common/Field'
import { Icon } from '@/components/common/Icon'
import { LeadTable } from '@/components/domain/LeadTable'
import { CreateCompanyWizard } from '@/components/domain/CreateCompanyWizard'
import { leadsService } from '@/services'
import { useLeadsViewModel } from '@/viewmodels/useLeadsViewModel'
import { FUNNEL_STAGES, CADENCE_STATUS } from '@/models/enums'
import styles from './Leads.module.css'

export function Leads() {
  const vm = useLeadsViewModel()
  useEffect(() => {
    const timer = setInterval(() => vm.reload(), 60000)
    return () => clearInterval(timer)
  }, [vm.reload])
  const [wizardOpen, setWizardOpen] = useState(false)
  const [filtersOpen, setFiltersOpen] = useState(false)
  const [saving, setSaving] = useState(false)
  const [wizardError, setWizardError] = useState(null)

  const createLead = async (values) => {
    setSaving(true)
    setWizardError(null)
    try {
      const created = await leadsService.create(values)
      // Refresh the listing to reflect the real backend state
      await vm.reload()
      window.dispatchEvent(new Event('leadway:leads-changed'))
      return created
    } catch (err) {
      // Propagate the error to the caller so the form can map field errors
      setWizardError(null) // O próprio formulário apresenta a mensagem detalhada
      throw err
    } finally {
      setSaving(false)
    }
  }

  const stats = [
    { label: 'Todos os leads', value: vm.stats?.total ?? 0 },
    { label: 'Em prospecção', value: vm.stats?.prospecting ?? 0 },
    { label: 'Follow-up', value: vm.stats?.followUp ?? 0 },
    { label: 'Precisam de atenção', value: vm.stats?.attention ?? 0, attention: true },
  ]
  const filterCount = Number(Boolean(vm.owner)) + Number(Boolean(vm.cadence)) + Number(vm.status !== 'active')

  return (
    <div className={styles.page}>
      <div className={styles.breadcrumb}>Inicial <span>›</span> <strong>Leads</strong></div>
      <div className={styles.heading}>
        <div><h1>Leads</h1><p>Gerencie sua carteira de empresas e acompanhe cada oportunidade.</p></div>
        <Button kind="primary" icon="plus" onClick={() => setWizardOpen(true)}>Cadastrar empresa</Button>
      </div>

      <div className={styles.stats} aria-label="Indicadores de leads">
        {stats.map((item) => <div key={item.label} className={styles.stat}>
          <span>{item.label}</span><strong className={item.attention ? styles.attention : ''}>{item.value}</strong>
        </div>)}
      </div>

      <div className={styles.tableCard}>
        <div className={styles.toolbar}>
          <div className={styles.search}><Field icon="search" placeholder="Buscar empresa pelo nome, CNPJ ou responsável..." value={vm.searchInput} onChange={vm.changeSearch} /></div>
          <div className={styles.filterWrapper}>
            <Button onClick={() => setFiltersOpen((current) => !current)} aria-expanded={filtersOpen}>
              <Icon name="filter" size={15} /> Filtros {filterCount > 0 && <span className={styles.filterCount}>{filterCount}</span>}
            </Button>
            {filtersOpen && <div className={styles.filterPopover}>
              <SelectField label="Responsável" value={vm.owner} onChange={vm.changeOwner}>
                <option value="">Todos os responsáveis</option>
                <option value="unassigned">Não atribuído</option>
                {vm.owners.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}
              </SelectField>
              <SelectField label="Status" value={vm.status} onChange={vm.changeStatus}>
                <option value="active">Ativos</option>
                <option value="inactive">Inativos</option>
                <option value="all">Todos</option>
              </SelectField>
              <SelectField label="Cadência" value={vm.cadence} onChange={vm.changeCadence}>
                <option value="">Todas as cadências</option>
                {Object.entries(CADENCE_STATUS).map(([key, item]) => <option key={key} value={key}>{item.label}</option>)}
              </SelectField>

            </div>}
          </div>
          <SelectField value={vm.stage} onChange={vm.changeStage} className={styles.filter}>
            <option value="">Todas as fases</option>
            {FUNNEL_STAGES.map((stage) => <option key={stage} value={stage}>{stage}</option>)}
          </SelectField>
        </div>
        {vm.activeFilters.length > 0 && <div className={styles.chips}>
          {vm.activeFilters.map((chip) => <span key={chip}>{chip}</span>)}
          <button type="button" onClick={vm.clearFilters}>Limpar filtros</button>
        </div>}
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
