import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { Badge } from '@/components/common/Badge'
import { Button } from '@/components/common/Button'
import { ErrorState, Skeleton } from '@/components/common/Feedback'
import { Icon } from '@/components/common/Icon'
import { Breadcrumb } from '@/components/common/PagePrimitives'
import { Panel, PanelHead } from '@/components/common/Panel'
import { companyInitials, scoreStars } from '@/models/lead'
import { CADENCE_STATUS } from '@/models/enums'
import { CadenceCard } from '@/components/domain/CadenceCard'
import { ContactList } from '@/components/domain/ContactList'
import { CreateCompanyWizard } from '@/components/domain/CreateCompanyWizard'
import { FunnelCard } from '@/components/domain/FunnelCard'
import { MiniStats } from '@/components/domain/MiniStats'
import { NewNoteModal } from '@/components/domain/NewNoteModal'
import { OwnerCard } from '@/components/domain/OwnerCard'
import { Timeline } from '@/components/domain/Timeline'
import { TransferModal } from '@/components/domain/TransferModal'
import { useLeadDetailViewModel } from '@/viewmodels/useLeadDetailViewModel'
import { paths } from '@/routes/paths'
import styles from './LeadDetail.module.css'

export function LeadDetail() {
  const { leadId } = useParams()
  const navigate = useNavigate()
  const vm = useLeadDetailViewModel(leadId)
  const [noteOpen, setNoteOpen] = useState(false)
  const [transferOpen, setTransferOpen] = useState(false)
  const [editOpen, setEditOpen] = useState(false)

  if (vm.loading) {
    return (
      <div className={styles.page}>
        <Skeleton lines={6} cards={2} />
      </div>
    )
  }

  if (vm.error) {
    return (
      <div className={styles.page}>
        <ErrorState message={vm.error} onRetry={vm.reload} />
        <div className={styles.backRow}>
          <Button onClick={() => navigate(paths.leads)}>Voltar para leads</Button>
        </div>
      </div>
    )
  }

  if (!vm.lead) {
    return (
      <div className={styles.page}>
        <Skeleton lines={6} cards={2} />
      </div>
    )
  }

  const lead = vm.lead
  const stars = scoreStars(lead.score)
  const cadence = CADENCE_STATUS[lead.cadenceStatus] ?? CADENCE_STATUS.ON_TRACK

  const miniStats = [
    { label: 'Último contato', value: lead.lastContact },
    { label: 'Cadência', value: cadence.label },
    { label: 'Próxima ação', value: lead.nextAction },
    { label: 'Etapa atual', value: lead.stage },
  ]

  return (
    <div className={styles.page}>
      <Breadcrumb
        items={[{ label: 'Leads', to: paths.leads }, { label: lead.name }]}
        onNavigate={(to) => navigate(to)}
      />

      <header className={styles.header}>
        <span className={styles.companyIcon}>{companyInitials(lead.name)}</span>
        <div className={styles.headerText}>
          <div className={styles.titleRow}>
            <h1>{lead.name}</h1>
            <Badge tone="neutral">{lead.stage}</Badge>
            <span className={styles.stars}>
              {stars.filled}
              <i>{stars.empty}</i>
            </span>
          </div>
          <p>
            {lead.cnpj}
            {lead.segment ? ` • ${lead.segment}` : ''}
            {lead.city ? ` • ${lead.city}${lead.state ? `/${lead.state}` : ''}` : ''}
          </p>
        </div>
        <div className={styles.headerActions}>
          <Button icon="plus" onClick={() => setNoteOpen(true)}>
            Nova anotação
          </Button>
          <Button icon="users" onClick={() => setTransferOpen(true)}>
            Alterar responsável
          </Button>
          <Button kind="primary" icon="settings" onClick={() => setEditOpen(true)}>
            Editar empresa
          </Button>
        </div>
      </header>

      {vm.actionError && <ErrorState message={vm.actionError} />}

      <MiniStats items={miniStats} />

      <div className={styles.columns}>
        <div className={styles.main}>
          <FunnelCard large funnel={vm.funnel} lead={lead} owner={vm.owner} />
          <CadenceCard large cadence={vm.cadence} />
          <Panel>
            <PanelHead
              title="Histórico de interações"
              subtitle="Contatos, reuniões e anotações registrados"
              actions={
                <Button size="sm" icon="plus" onClick={() => setNoteOpen(true)}>
                  Nova anotação
                </Button>
              }
            />
            <div className={styles.timeline}>
              <Timeline items={vm.timeline} extended />
            </div>
          </Panel>
        </div>

        <aside className={styles.side}>
          <OwnerCard owner={vm.owner} lead={lead} />

          <Panel>
            <PanelHead
              title="Contatos"
              subtitle={`${vm.contacts.length} pessoa(s) vinculada(s)`}
              actions={
                <Button kind="ghost" size="sm" onClick={() => navigate(paths.contacts)}>
                  Ver todos
                </Button>
              }
            />
            <div className={styles.contacts}>
              <ContactList contacts={vm.contacts} />
            </div>
          </Panel>

          <Panel>
            <PanelHead title="Próximos passos" />
            <div className={styles.nextSteps}>
              <span className={styles.nextStep}>
                <i>
                  <Icon name="clock" size={14} />
                </i>
                <div>
                  <small>PRÓXIMA ATIVIDADE</small>
                  <strong>{lead.nextAction}</strong>
                </div>
              </span>
              <span className={styles.nextStep}>
                <i>
                  <Icon name="phone" size={14} />
                </i>
                <div>
                  <small>CONTATO PRINCIPAL</small>
                  <strong>{vm.contacts[0]?.name ?? 'Sem contato'}</strong>
                </div>
              </span>
              <Button full icon="calendar" onClick={() => navigate(paths.agenda)}>
                Abrir agenda
              </Button>
            </div>
          </Panel>
        </aside>
      </div>

      {noteOpen && (
        <NewNoteModal
          leadName={lead.name}
          saving={vm.saving}
          error={vm.actionError}
          onClose={() => setNoteOpen(false)}
          onSubmit={vm.addNote}
        />
      )}

      {transferOpen && (
        <TransferModal
          lead={lead}
          saving={vm.saving}
          error={vm.actionError}
          onClose={() => setTransferOpen(false)}
          onConfirm={vm.transfer}
        />
      )}

      {editOpen && (
        <CreateCompanyWizard
          lead={lead}
          saving={vm.saving}
          error={vm.actionError}
          onClose={() => setEditOpen(false)}
          onSubmit={(values) => vm.update(values)}
        />
      )}
    </div>
  )
}

export default LeadDetail
