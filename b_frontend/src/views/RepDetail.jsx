import { useState } from 'react'
import { representativesService } from '@/services/representatives.service'
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
import { formatPhoneInput } from '@/utils/phoneInput'
import { CreateRepresentativeWizard } from '@/components/domain/CreateRepresentativeWizard'

export function RepDetail() {
  const { repId } = useParams()
  const navigate = useNavigate()
  const vm = useRepDetailViewModel(repId)
  const [editing, setEditing] = useState(false)
  const [form, setForm] = useState(null)
  const [saveError, setSaveError] = useState('')
  const [saving, setSaving] = useState(false)
  const startEdit = () => { const r=vm.representative; setForm({name:r.name,email:r.email,phone:r.phone,code:r.code,region:r.region==='—'?'':r.region,active:r.active,cargo:typeof r.cargo==='object'?r.cargo.nome:r.cargo}); setEditing(true); setSaveError('') }
  const save = async (event) => {event.preventDefault();setSaving(true);setSaveError('');try{await representativesService.update(repId,form);setEditing(false);vm.reload()}catch(e){setSaveError(e.message)}finally{setSaving(false)}}

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

      <header className={styles.profileCard}>
        <Avatar initials={rep.initials} size="xlg" />
        <div className={styles.headerText}>
          <div className={styles.titleRow}><h1>{rep.name}</h1>{rep.active ? <Badge tone="success">Ativo</Badge> : <Badge tone="neutral">Inativo</Badge>}</div>
          <p>{rep.code || 'Sem código'} • {typeof rep.cargo === 'object' ? rep.cargo.nome : rep.cargo || 'Vendedor'}</p>
          <p className={styles.contactLine}><span>✉ {rep.email || '—'}</span><span>☎ {formatPhoneInput(rep.phone) || '—'}</span><span>⌖ {rep.region || '—'}</span></p>
        </div>
        <div className={styles.headerActions}><Button kind="primary" onClick={startEdit}>Editar dados</Button></div>
      </header>

      {editing && <CreateRepresentativeWizard representative={form} saving={saving} error={saveError} onClose={() => setEditing(false)} onSubmit={async (values) => { setSaving(true); setSaveError(''); try { await representativesService.update(repId, values); await vm.reload(); return true } catch (e) { setSaveError(e.message); return false } finally { setSaving(false) } }} />}
      {vm.notice && (
        <InlineMessage tone="warning" title={vm.notice.title} description={vm.notice.description} />
      )}

      <div className={styles.stats}>
        <StatCard label="Leads em carteira" value={vm.leads.length} icon="briefcase" />
        <StatCard label="Leads em proposta" value={vm.leads.filter(l => Number(l.etapa?.ordem) === 3).length} icon="phone" />
        <StatCard label="Pendências identificadas" value={rep.pending ?? 0} icon="calendar" />
        <StatCard label="Leads ativos" value={vm.leads.filter(l => l.ativo !== false && l.active !== false && l.ativo !== 0).length} icon="trending-up" />
      </div>

      <div className={styles.columns}>
        <Panel>
          <PanelHead title="Evolução do funcionário" subtitle="Atividades registradas" />
          <div className={styles.emptyChart}>O histórico mensal de atividades não está disponível na API atual. Nenhum dado fictício é exibido.</div>
        </Panel>

        <Panel>
          <PanelHead title="Informações" />
          <div className={styles.summary}>
            <span>
              <small>REGIÃO DE ATENDIMENTO</small>
              <strong>{rep.region}</strong>
            </span>
            <span>
              <small>CÓDIGO DO REPRESENTANTE</small>
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
        <PanelHead title="Empresas vinculadas" subtitle={`${vm.leads.length} empresa(s) na carteira`} actions={<Button onClick={() => navigate(paths.leads)}>Ver carteira completa</Button>} />
        <LeadTable items={vm.leads} loading={false} error={null} compact />
      </div>
    </div>
  )
}

export default RepDetail
