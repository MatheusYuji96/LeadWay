import { useNavigate } from 'react-router-dom'
import { Badge } from '@/components/common/Badge'
import { Button } from '@/components/common/Button'
import { ErrorState, Skeleton } from '@/components/common/Feedback'
import { Icon } from '@/components/common/Icon'
import { Panel, PanelHead } from '@/components/common/Panel'
import { StatCard } from '@/components/common/StatCard'
import { Tabs, PageHeading } from '@/components/common/PagePrimitives'
import { useDashboardViewModel } from '@/viewmodels/useDashboardViewModel'
import { paths } from '@/routes/paths'
import styles from './Dashboard.module.css'

const TABS = [
  { value: 'overview', label: 'Minha visão' },
  { value: 'team', label: 'Acompanhamento da equipe' },
]

const LEGEND = [
  { key: 'contacts', label: 'Contatos', tone: 'brand' },
  { key: 'meetings', label: 'Reuniões', tone: 'blue' },
  { key: 'followUps', label: 'Follow-ups', tone: 'purple' },
]

export function Dashboard() {
  const navigate = useNavigate()
  const vm = useDashboardViewModel()

  const todayLabel = 'Quinta-feira, 15 de maio de 2025'

  if (vm.overviewLoading && vm.tab === 'overview') {
    return (
      <div className={styles.page}>
        <PageHeading eyebrow={todayLabel} title="Visão geral" description="Acompanhe sua carteira e seus compromissos." />
        <Skeleton cards={4} lines={3} />
      </div>
    )
  }

  if (vm.overviewError && vm.tab === 'overview') {
    return (
      <div className={styles.page}>
        <PageHeading eyebrow={todayLabel} title="Visão geral" />
        <ErrorState message={vm.overviewError} onRetry={vm.reloadOverview} />
      </div>
    )
  }

  const overview = vm.overview

  return (
    <div className={styles.page}>
      <PageHeading
        eyebrow={todayLabel}
        title="Visão geral"
        description="Acompanhe sua carteira, seus compromissos e o desempenho da equipe."
        actions={
          <>
            <Button icon="calendar" onClick={() => navigate(paths.agenda)}>
              Ver agenda
            </Button>
            <Button kind="primary" icon="plus" onClick={() => navigate(paths.leads)}>
              Novo lead
            </Button>
          </>
        }
      />

      <Tabs tabs={TABS} active={vm.tab} onChange={vm.setTab} />

      {vm.tab === 'overview' && overview && (
        <div className={styles.content}>
          <div className={styles.statsGrid}>
            {overview.stats.map((stat) => (
              <StatCard key={stat.key} {...stat} />
            ))}
          </div>

          <div className={styles.columns}>
            <div className={styles.main}>
              <Panel>
                <PanelHead title="Atividade da semana" subtitle="Contatos, reuniões e follow-ups por dia" />
                <WeekChart data={overview.weekActivity} />
              </Panel>

              <Panel>
                <PanelHead
                  title="Leads que precisam de atenção"
                  subtitle="Sem atualização nos últimos dias"
                  actions={
                    <Button kind="ghost" size="sm" onClick={() => navigate(paths.leads)}>
                      Ver todos <Icon name="arrow" size={14} />
                    </Button>
                  }
                />
                <div className={styles.attentionList}>
                  {overview.leadsNeedingAttention.length === 0 && (
                    <p className={styles.emptyText}>Nenhum lead precisa de atenção agora.</p>
                  )}
                  {overview.leadsNeedingAttention.map((lead) => (
                    <button
                      type="button"
                      className={styles.attentionRow}
                      key={lead.id}
                      onClick={() => navigate(`${paths.leads}/${lead.id}`)}
                    >
                      <span>
                        <strong>{lead.name}</strong>
                        <small>
                          {lead.representativeName} • {lead.stage}
                        </small>
                      </span>
                      <Badge tone={lead.daysInStage > 7 ? 'danger' : 'warning'}>
                        {lead.lastContact}
                      </Badge>
                    </button>
                  ))}
                </div>
              </Panel>
            </div>

            <aside className={styles.side}>
              <Panel>
                <PanelHead
                  title="Compromissos de hoje"
                  actions={
                    <Button kind="ghost" size="sm" onClick={() => navigate(paths.agenda)}>
                      Agenda
                    </Button>
                  }
                />
                <div className={styles.appointmentList}>
                  {overview.appointments.length === 0 && (
                    <p className={styles.emptyText}>Nenhum compromisso para hoje.</p>
                  )}
                  {overview.appointments.map((appt) => (
                    <div className={styles.appointment} key={appt.id}>
                      <span className={styles.apptTime}>{appt.time}</span>
                      <div>
                        <strong>{appt.type}</strong>
                        <small>{appt.companyName}</small>
                        <span>{appt.contactName}</span>
                      </div>
                      <i className={styles[`dot-${appt.tone}`]} />
                    </div>
                  ))}
                </div>
              </Panel>

              <Panel>
                <PanelHead title="Atalhos" />
                <div className={styles.shortcuts}>
                  <Button full icon="plus" onClick={() => navigate(paths.leads)}>
                    Cadastrar lead
                  </Button>
                  <Button full icon="users" onClick={() => navigate(paths.team)}>
                    Ver equipe
                  </Button>
                  <Button full icon="settings" onClick={() => navigate(paths.settings)}>
                    Configurações
                  </Button>
                </div>
              </Panel>
            </aside>
          </div>
        </div>
      )}

      {vm.tab === 'team' && <TeamPanel vm={vm} onOpenLead={(id) => navigate(`${paths.leads}/${id}`)} onOpenRep={(id) => navigate(`${paths.team}/${id}`)} />}
    </div>
  )
}

function TeamPanel({ vm, onOpenLead, onOpenRep }) {
  if (vm.teamLoading) return <Skeleton cards={4} lines={4} />
  if (vm.teamError) return <ErrorState message={vm.teamError} onRetry={vm.reloadTeam} />
  if (!vm.team) return null

  const { stats, performance, staleLeads, representatives } = vm.team
  const maxContacts = Math.max(...performance.map((item) => item.contacts), 1)

  return (
    <div className={styles.content}>
      <div className={styles.statsGrid}>
        {stats.map((stat) => (
          <StatCard key={stat.key} {...stat} />
        ))}
      </div>

      <div className={styles.columns}>
        <div className={styles.main}>
          <Panel>
            <PanelHead title="Desempenho da equipe" subtitle="Contatos realizados no mês por representante" />
            <div className={styles.performance}>
              {performance.map((item) => (
                <div className={styles.perfRow} key={item.name}>
                  <strong>{item.name}</strong>
                  <span className={styles.perfBar}>
                    <i style={{ width: `${(item.contacts / maxContacts) * 100}%` }} />
                  </span>
                  <span>{item.contacts} contatos</span>
                  <span>{item.meetings} reuniões</span>
                  <span>{item.visits} visitas</span>
                </div>
              ))}
            </div>
          </Panel>

          <Panel>
            <PanelHead title="Leads parados" subtitle="Oportunidades sem atualização há mais de 7 dias" />
            <div className={styles.staleList}>
              {staleLeads.length === 0 && <p className={styles.emptyText}>Nenhum lead parado.</p>}
              {staleLeads.map((lead) => (
                <button type="button" className={styles.staleRow} key={lead.id} onClick={() => onOpenLead(lead.id)}>
                  <span>
                    <strong>{lead.name}</strong>
                    <small>
                      {lead.representativeName} • {lead.stage}
                    </small>
                  </span>
                  <Badge tone="danger">{lead.days} dias parado</Badge>
                </button>
              ))}
            </div>
          </Panel>
        </div>

        <aside className={styles.side}>
          <Panel>
            <PanelHead title="Representantes" />
            <div className={styles.repList}>
              {representatives.map((rep) => (
                <button type="button" className={styles.repRow} key={rep.id} onClick={() => onOpenRep(rep.id)}>
                  <span>{rep.initials}</span>
                  <div>
                    <strong>{rep.name}</strong>
                    <small>{rep.code}</small>
                  </div>
                  <i>{rep.pending}</i>
                </button>
              ))}
            </div>
          </Panel>
        </aside>
      </div>
    </div>
  )
}

function WeekChart({ data = [] }) {
  const max = Math.max(...data.flatMap((item) => [item.contacts, item.meetings, item.followUps]), 1)

  return (
    <div className={styles.chart}>
      <div className={styles.chartBody}>
        {data.map((item) => (
          <div className={styles.chartGroup} key={item.day}>
            <span className={styles.bars}>
              <i className={styles.barBrand} style={{ height: `${(item.contacts / max) * 100}%` }} title={`${item.contacts} contatos`} />
              <i className={styles.barBlue} style={{ height: `${(item.meetings / max) * 100}%` }} title={`${item.meetings} reuniões`} />
              <i className={styles.barPurple} style={{ height: `${(item.followUps / max) * 100}%` }} title={`${item.followUps} follow-ups`} />
            </span>
            <small>{item.day}</small>
          </div>
        ))}
      </div>
      <div className={styles.legend}>
        {LEGEND.map((item) => (
          <span key={item.key}>
            <i className={styles[`legend-${item.key}`]} /> {item.label}
          </span>
        ))}
      </div>
    </div>
  )
}

export default Dashboard
