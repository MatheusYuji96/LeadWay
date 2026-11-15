import { useNavigate, useParams } from 'react-router-dom'
import { Avatar } from '@/components/common/Avatar'
import { Badge } from '@/components/common/Badge'
import { Button } from '@/components/common/Button'
import { ErrorState, InlineMessage, Skeleton } from '@/components/common/Feedback'
import { Breadcrumb } from '@/components/common/PagePrimitives'
import { Panel, PanelHead } from '@/components/common/Panel'
import { StatCard } from '@/components/common/StatCard'
import { LeadTable } from '@/components/domain/LeadTable'
import { useRepDetailViewModel } from '@/viewmodels/useRepDetailViewModel'
import { paths } from '@/routes/paths'
import styles from './RepDetail.module.css'

export function RepDetail() {
  const { repId } = useParams()
  const navigate = useNavigate()
  const vm = useRepDetailViewModel(repId)

  if (vm.loading) {
    return (
      <div className={styles.page}>
        <Skeleton lines={5} cards={4} />
      </div>
    )
  }

  if (vm.error) {
    return (
      <div className={styles.page}>
        <ErrorState message={vm.error} onRetry={vm.reload} />
        <div className={styles.backRow}>
          <Button onClick={() => navigate(paths.team)}>Voltar para equipe</Button>
        </div>
      </div>
    )
  }

  if (!vm.representative) {
    return (
      <div className={styles.page}>
        <Skeleton lines={5} cards={4} />
      </div>
    )
  }

  const rep = vm.representative
  const maxEvolution = Math.max(...vm.evolution.map((item) => item.value), 1)

  return (
    <div className={styles.page}>
      <Breadcrumb items={[{ label: 'Equipe', to: paths.team }, { label: rep.name }]} onNavigate={(to) => navigate(to)} />

      <header className={styles.header}>
        <Avatar initials={rep.initials} size="xlg" />
        <div className={styles.headerText}>
          <div className={styles.titleRow}>
            <h1>{rep.name}</h1>
            {rep.active ? <Badge tone="success">Ativo</Badge> : <Badge tone="neutral">Inativo</Badge>}
          </div>
          <p>
            {rep.code} • {rep.region} • {rep.email || '—'}
          </p>
        </div>
        <div className={styles.headerActions}>
          <Button icon="briefcase" onClick={() => navigate(paths.leads)}>
            Ver leads da carteira
          </Button>
        </div>
      </header>

      {vm.notice && (
        <InlineMessage tone="warning" title={vm.notice.title} description={vm.notice.description} />
      )}

      <div className={styles.stats}>
        {vm.stats.map((stat) => (
          <StatCard key={stat.key} {...stat} />
        ))}
      </div>

      <div className={styles.columns}>
        <Panel>
          <PanelHead title="Evolução de contatos" subtitle="Últimos 6 meses" />
          <div className={styles.evolution}>
            <div className={styles.evolutionBars}>
              {vm.evolution.map((item) => (
                <div className={styles.evoItem} key={item.month}>
                  <span className={styles.evoBar}>
                    <i style={{ height: `${(item.value / maxEvolution) * 100}%` }} title={`${item.value} contatos`} />
                    <b>{item.value}</b>
                  </span>
                  <small>{item.month}</small>
                </div>
              ))}
            </div>
          </div>
        </Panel>

        <Panel>
          <PanelHead title="Resumo" />
          <div className={styles.summary}>
            <span>
              <small>Região</small>
              <strong>{rep.region}</strong>
            </span>
            <span>
              <small>Código</small>
              <strong>{rep.code}</strong>
            </span>
            <span>
              <small>Leads na carteira</small>
              <strong>{rep.leads}</strong>
            </span>
            <span>
              <small>Contatos realizados</small>
              <strong>{rep.completed ?? 0}</strong>
            </span>
            <span>
              <small>Pendências</small>
              <strong>{rep.pending ?? 0}</strong>
            </span>
          </div>
        </Panel>
      </div>

      <div className={styles.tableCard}>
        <PanelHead title="Leads do representante" subtitle={`${vm.leads.length} empresa(s) na carteira`} />
        <LeadTable items={vm.leads} loading={false} error={null} compact />
      </div>
    </div>
  )
}

export default RepDetail
