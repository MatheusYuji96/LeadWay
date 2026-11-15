import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { Button } from '@/components/common/Button'
import { ErrorState, Skeleton } from '@/components/common/Feedback'
import { Icon } from '@/components/common/Icon'
import { Breadcrumb } from '@/components/common/PagePrimitives'
import { Panel, PanelHead } from '@/components/common/Panel'
import { companyInitials, scoreStars } from '@/models/lead'
import { ContactList } from '@/components/domain/ContactList'
import { CreateCompanyWizard } from '@/components/domain/CreateCompanyWizard'
import { FunnelCard } from '@/components/domain/FunnelCard'
import { NewNoteModal } from '@/components/domain/NewNoteModal'
import { OwnerCard } from '@/components/domain/OwnerCard'
import { Timeline } from '@/components/domain/Timeline'
import { TransferModal } from '@/components/domain/TransferModal'
import { useLeadDetailViewModel } from '@/viewmodels/useLeadDetailViewModel'
import { paths } from '@/routes/paths'
import styles from './LeadDetail.module.css'

const tabs = [
  ['overview', 'Visão geral', 'home'], ['company', 'Empresa', 'building'],
  ['contacts', 'Contatos', 'users'], ['history', 'Histórico', 'clock'],
  ['sales', 'Processo comercial', 'trend'],
]
const formatDate = (value) => {
  if (!value) return '—'
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? '—' : date.toLocaleDateString('pt-BR')
}

export function LeadDetail() {
  const { leadId } = useParams()
  const navigate = useNavigate()
  const vm = useLeadDetailViewModel(leadId)
  const [tab, setTab] = useState('overview')
  const [noteOpen, setNoteOpen] = useState(false)
  const [transferOpen, setTransferOpen] = useState(false)
  const [editOpen, setEditOpen] = useState(false)
  const [editStartStep, setEditStartStep] = useState(0)
  const openEdit = (step = 0) => { setEditStartStep(step); setEditOpen(true) }
  if (vm.loading && !vm.lead) return <div className={styles.page}><Skeleton lines={6} cards={2} /></div>
  if (vm.error) return <div className={styles.page}><ErrorState message={vm.error} onRetry={vm.reload} /><Button onClick={() => navigate(paths.leads)}>Voltar</Button></div>
  if (!vm.lead) return <div className={styles.page}><Skeleton lines={6} cards={2} /></div>
  const lead = vm.lead
  const stars = scoreStars(lead.score)
  const primary = vm.contacts.find(c => c.classificacao === 'DECISOR') ?? vm.contacts[0]
  const confirmDeleteInteraction = async (id) => {
    if (window.confirm('Excluir esta interação permanentemente?')) await vm.deleteInteraction(id)
  }
  const history = (full = false) => <Panel><PanelHead title={full ? 'Histórico de interações' : 'Últimas interações'} subtitle="Movimentações registradas neste relacionamento" actions={<div className={styles.panelActions}>{!full && <Button size="sm" onClick={() => setTab('history')}>Ver histórico completo</Button>}<Button size="sm" kind="primary" onClick={() => setNoteOpen(true)}>Nova interação</Button></div>} /><div className={styles.timeline}><Timeline items={full ? vm.timeline : vm.timeline.slice(0, 3)} extended onDelete={confirmDeleteInteraction} /></div></Panel>
  const owner = <div className={styles.ownerWrap}>{vm.owner ? <OwnerCard owner={vm.owner} lead={lead} /> : <Panel><PanelHead title="Responsável" /><p className={styles.pad}>Nenhum vendedor atribuído.</p></Panel>}<Button size="sm" onClick={() => setTransferOpen(true)}>Alterar responsável</Button></div>
  return <div className={styles.page}>
    <Breadcrumb items={[{ label: 'Leads', to: paths.leads }, { label: lead.name }]} onNavigate={navigate} />
    <header className={styles.header}>
      <span className={styles.companyIcon}>{companyInitials(lead.name)}</span>
      <div className={styles.headerText}><div className={styles.titleRow}><h1>{lead.name}</h1><span className={styles.pillSuccess}>• {lead.ativo === false ? 'Inativo' : 'Ativo'}</span>{lead.prioridade && <span className={styles.pillPriority}>• Prioridade {lead.prioridade}</span>}</div>
        <p>CNPJ {lead.cnpj || '—'} • {lead.segment || 'Setor não informado'}</p>
        <p>Potencial do lead <span className={styles.stars}>{stars.filled}<i>{stars.empty}</i></span></p>
      </div>
      <div className={styles.headerActions}><Button onClick={() => setTransferOpen(true)}>Transferir responsável</Button><Button kind="primary" onClick={() => openEdit(0)}>Editar empresa</Button></div>
    </header>
    {vm.actionError && <ErrorState message={vm.actionError} />}
    <nav className={styles.tabs} aria-label="Seções da empresa">{tabs.map(([id,label,icon]) => <button key={id} type="button" aria-current={tab === id ? 'page' : undefined} className={tab === id ? styles.tabActive : styles.tab} onClick={() => setTab(id)}><Icon name={icon} size={16}/>{label}{id === 'contacts' && <span className={styles.tabCount}>{vm.contacts.length}</span>}</button>)}</nav>
    {tab === 'overview' && <>
      <section className={styles.summaryGrid}>
        <div className={styles.summaryCard}><span className={styles.summaryIcon}><Icon name="trend" /></span><div><small>ETAPA ATUAL</small><strong>{lead.stage}</strong><p>Etapa atual da oportunidade</p><button onClick={() => setTab('sales')}>Ver processo →</button></div></div>
        <div className={styles.summaryCard}><span className={styles.summaryIcon}><Icon name="calendar" /></span><div><small>PRÓXIMA ATIVIDADE</small><strong>Não agendada</strong><p>A API atual não fornece a próxima tarefa por Lead.</p><button onClick={() => setTab('sales')}>Ver processo →</button></div></div>
        <div className={styles.summaryCard}><span className={styles.summaryIcon}><Icon name="users" /></span><div><small>CONTATO PRINCIPAL</small><strong>{primary?.name ?? 'Sem contato'}</strong><p>{primary?.role ?? 'Cadastre um contato para a empresa'}</p><button onClick={() => setTab('contacts')}>Ver contatos →</button></div></div>
      </section>
      <div className={styles.columns}><div className={styles.main}>{history()}</div><aside className={styles.side}>{owner}</aside></div>
    </>}
    {tab === 'company' && <Panel><PanelHead title="Dados da empresa" actions={<Button size="sm" onClick={() => openEdit(0)}>Editar dados</Button>} /><dl className={styles.details}>{[['Razão social', lead.razaoSocial],['Nome fantasia', lead.name],['CNPJ', lead.cnpj],['Setor', lead.segment],['E-mail', lead.email],['Telefone', lead.phone],['Potencial', `${lead.score || 0} estrelas`],['Cidade', lead.city],['Estado', lead.state],['Endereço', [lead.endereco?.rua, lead.endereco?.numero, lead.endereco?.bairro].filter(Boolean).join(', ')],['Criado em', formatDate(lead.criadoEm)]].map(([label,value]) => <div key={label}><dt>{label}</dt><dd>{value || '—'}</dd></div>)}</dl></Panel>}
    {tab === 'contacts' && <Panel><PanelHead title="Contatos vinculados" subtitle={`${vm.contacts.length} contato(s)`} actions={<Button size="sm" kind="primary" onClick={() => openEdit(3)}>Gerenciar contatos</Button>} /><div className={styles.pad}><ContactList contacts={vm.contacts} /></div></Panel>}
    {tab === 'history' && history(true)}
    {tab === 'sales' && <div className={styles.columns}><div className={styles.main}><FunnelCard large funnel={vm.funnel} lead={lead} owner={vm.owner} onUpdateStage={() => openEdit(2)} /><Panel><PanelHead title="Próxima etapa do funil"/><p className={styles.pad}>{lead.nextAction || '—'}</p></Panel></div><aside className={styles.side}>{owner}</aside></div>}
    {noteOpen && <NewNoteModal leadName={lead.name} contacts={vm.contacts} saving={vm.saving} error={vm.actionError} onClose={() => setNoteOpen(false)} onSubmit={vm.addNote} />}
    {transferOpen && <TransferModal lead={lead} saving={vm.saving} error={vm.actionError} onClose={() => setTransferOpen(false)} onConfirm={vm.transfer} />}
    {editOpen && <CreateCompanyWizard lead={lead} initialStep={editStartStep} saving={vm.saving} error={vm.actionError} onClose={() => { setEditOpen(false); vm.reload() }} onSubmit={vm.update} />}
  </div>
}
export default LeadDetail
