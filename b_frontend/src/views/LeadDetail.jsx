import { useState } from 'react'
import { leadsService } from '@/services/leads.service'
import { useNavigate, useParams } from 'react-router-dom'
import { Button } from '@/components/common/Button'
import { ErrorState, Skeleton } from '@/components/common/Feedback'
import { Icon } from '@/components/common/Icon'
import { Breadcrumb } from '@/components/common/PagePrimitives'
import { Panel, PanelHead } from '@/components/common/Panel'
import { companyInitials, scoreStars } from '@/models/lead'
import { ContactList } from '@/components/domain/ContactList'
import { EditContactModal } from '@/components/domain/EditContactModal'
import { CreateCompanyWizard } from '@/components/domain/CreateCompanyWizard'
import { FunnelCard } from '@/components/domain/FunnelCard'
import { LeadCadencePanel } from '@/components/domain/LeadCadencePanel'
import { CommercialProcessModal } from '@/components/domain/CommercialProcessModal'
import { ConfirmActionModal } from '@/components/common/ConfirmActionModal'
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
const formatCnpj = (value) => {
  const digits = String(value ?? '').replace(/\D/g, '')
  return digits.length === 14 ? digits.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/, '$1.$2.$3/$4-$5') : value || '—'
}
const formatPhone = (value) => {
  const d = String(value ?? '').replace(/\D/g, '')
  return d.length === 11 ? d.replace(/^(\d{2})(\d{5})(\d{4})$/, '($1) $2-$3') : d.length === 10 ? d.replace(/^(\d{2})(\d{4})(\d{4})$/, '($1) $2-$3') : value || '—'
}
const formatCep = (value) => String(value ?? '').replace(/\D/g, '').replace(/^(\d{5})(\d{3})$/, '$1-$2')
const priorityClass = (value) => ({ BAIXA: styles.priorityLow, MEDIA: styles.priorityMedium, ALTA: styles.priorityHigh })[String(value ?? '').toUpperCase()] ?? styles.priorityUnset
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
  const [commercialMode, setCommercialMode] = useState(null)
  const [deleteTarget, setDeleteTarget] = useState(null)
  const [editingContact, setEditingContact] = useState(null)
  const [editStartStep, setEditStartStep] = useState(0)
  const openEdit = (step = 0) => { setEditStartStep(step); setEditOpen(true) }
  if (vm.loading && !vm.lead) return <div className={styles.page}><Skeleton lines={6} cards={2} /></div>
  if (vm.error) return <div className={styles.page}><ErrorState message={vm.error} onRetry={vm.reload} /><Button onClick={() => navigate(paths.leads)}>Voltar</Button></div>
  if (!vm.lead) return <div className={styles.page}><Skeleton lines={6} cards={2} /></div>
  const lead = vm.lead
  const stars = scoreStars(lead.score)
  const primary = vm.contacts.find(c => c.classificacao === 'PRINCIPAL') ?? vm.contacts[0]
  const confirmDeleteInteraction = async (id) => {
    const interaction = vm.timeline.find(item => String(item.id) === String(id))
    setDeleteTarget({ title: 'Excluir interação?', description: `A interação “${interaction?.title ?? 'selecionada'}” será removida do histórico. Esta ação não poderá ser desfeita.`, action: () => vm.deleteInteraction(id) })
  }
  const history = (full = false) => <Panel><PanelHead title={full ? 'Histórico de interações' : 'Últimas interações'} subtitle={full ? 'Todas as interações registradas em ordem cronológica' : 'Movimentações recentes deste relacionamento'} actions={<div className={styles.panelActions}>{!full && <Button size="sm" onClick={() => setTab('history')}>Ver histórico completo</Button>}<Button size="sm" kind="primary" onClick={() => setNoteOpen(true)}>Nova interação</Button></div>} /><div className={styles.timeline}><Timeline items={full ? vm.timeline : vm.timeline.slice(0, 3)} extended onDelete={confirmDeleteInteraction} /></div></Panel>
  const owner = <div className={styles.ownerWrap}>{vm.owner ? <OwnerCard owner={vm.owner} lead={lead} onChange={() => setTransferOpen(true)} /> : <Panel><PanelHead title="Responsável" /><p className={styles.pad}>Nenhum vendedor atribuído.</p></Panel>}</div>
  return <div className={styles.page}>
    <Breadcrumb items={[{ label: 'Leads', to: paths.leads }, { label: lead.name }]} onNavigate={navigate} />
    <header className={styles.header}>
      <span className={styles.companyIcon}>{companyInitials(lead.name)}</span>
      <div className={styles.headerText}><div className={styles.titleRow}><h1>{lead.name}</h1><span className={lead.ativo === false ? styles.pillInactive : styles.pillSuccess}>• {lead.ativo === false ? 'Inativo' : 'Ativo'}</span>{lead.prioridade && <span className={`${styles.pillPriority} ${priorityClass(lead.prioridade)}`}>• Prioridade {lead.prioridade}</span>}</div>
        <p>CNPJ {formatCnpj(lead.cnpj)} • {lead.segment || 'Setor não informado'}</p>
        <p>Potencial do lead <span className={styles.stars}>{stars.filled}<i>{stars.empty}</i></span></p>
      </div>
      <div className={styles.headerActions}><Button onClick={() => setTransferOpen(true)}>Transferir responsável</Button><Button kind="primary" onClick={() => openEdit(0)}>Editar empresa</Button><Button onClick={() => setDeleteTarget({ title: "Desativar lead?", description: "O lead será desativado e deixará de aparecer nas carteiras ativas. Esta ação não exclui o histórico.", confirmLabel: "Desativar lead", action: async () => { await leadsService.delete(leadId); navigate(paths.leads) } })} disabled={lead.ativo === false}>Desativar lead</Button></div>
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
    {tab === 'company' && <Panel className={styles.companyPanel}>
      <PanelHead title="Dados da empresa" subtitle="Informações cadastrais e canais de contato" actions={<Button size="sm" kind="primary" onClick={() => openEdit(0)}>Editar dados</Button>} />
      <div className={styles.companyGrid}>
        {[
          ['briefcase', 'SETOR', lead.segment],
          ['building', 'CNPJ', formatCnpj(lead.cnpj)],
          ['phone', 'TELEFONE', formatPhone(lead.phone)],
          ['mail', 'E-MAIL', lead.email],
          ['trend', 'POTENCIAL', `${lead.score || 0} de 5`],
        ].map(([icon,label,value]) => <div className={styles.companyField} key={label}><span className={styles.fieldIcon}><Icon name={icon} size={18} /></span><div><small>{label}</small><strong>{value || '—'}</strong></div></div>)}
      </div>
      <div className={styles.addressBox}><Icon name="map" size={20} /><div><h3>ENDEREÇO PARA VISITAS</h3><strong>{[lead.endereco?.rua ?? lead.address?.street, lead.endereco?.numero ?? lead.address?.number, lead.endereco?.complemento ?? lead.address?.complement].filter(Boolean).join(', ') || 'Endereço não informado'}</strong><p>{[lead.endereco?.bairro ?? lead.address?.neighborhood, lead.city, lead.state].filter(Boolean).join(', ')}{(lead.endereco?.cep ?? lead.address?.zipCode) ? ` • CEP ${formatCep(lead.endereco?.cep ?? lead.address?.zipCode)}` : ''}</p></div></div>
    </Panel>}
    {tab === 'contacts' && <Panel className={styles.contactsPanel}><PanelHead title="Contatos" subtitle={`${vm.contacts.length} pessoa(s) vinculada(s) a esta empresa`} actions={<Button size="sm" kind="primary" onClick={() => openEdit(3)}>+ Adicionar contato</Button>} /><div className={styles.pad}><ContactList contacts={vm.contacts} onEdit={setEditingContact} onDelete={(contact) => setDeleteTarget({ title: 'Excluir contato?', description: `O contato “${contact.name}” será removido. Esta ação não poderá ser desfeita.`, action: () => vm.deleteContact(contact.id) })} /></div></Panel>}
    {tab === 'history' && history(true)}
    {tab === 'sales' && <div className={styles.commercialGrid}><FunnelCard large funnel={vm.funnel} lead={lead} owner={vm.owner} onUpdateStage={() => setCommercialMode("funnel")} /><LeadCadencePanel leadId={leadId} contacts={vm.contacts} onConfigure={() => setCommercialMode("cadence")} /></div>}
    {commercialMode && <CommercialProcessModal mode={commercialMode} lead={lead} leadId={leadId} contacts={vm.contacts} owner={vm.owner} onClose={() => setCommercialMode(null)} onStageUpdate={vm.update} onSaved={vm.reload} />}
    {deleteTarget && <ConfirmActionModal title={deleteTarget.title} description={deleteTarget.description} confirmLabel={deleteTarget.confirmLabel} onClose={() => setDeleteTarget(null)} onConfirm={deleteTarget.action} />}
    {editingContact && <EditContactModal contact={editingContact} saving={vm.saving} onClose={() => setEditingContact(null)} onSave={vm.updateContact} />}
    {noteOpen && <NewNoteModal leadName={lead.name} contacts={vm.contacts} saving={vm.saving} error={vm.actionError} onClose={() => setNoteOpen(false)} onSubmit={vm.addNote} />}
    {transferOpen && <TransferModal lead={lead} saving={vm.saving} error={vm.actionError} onClose={() => setTransferOpen(false)} onConfirm={vm.transfer} />}
    {editOpen && <CreateCompanyWizard lead={lead} initialStep={editStartStep} saving={vm.saving} error={vm.actionError} onClose={() => { setEditOpen(false); vm.reload() }} onSubmit={vm.update} />}
  </div>
}
export default LeadDetail
