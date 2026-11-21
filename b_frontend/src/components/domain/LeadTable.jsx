import { useState } from 'react'
import { ConfirmActionModal } from '@/components/common/ConfirmActionModal'
import { useNavigate } from 'react-router-dom'
import { Badge } from '@/components/common/Badge'
import { Button } from '@/components/common/Button'
import { Icon } from '@/components/common/Icon'
import { EmptyState, ErrorState, Skeleton } from '@/components/common/Feedback'
import { companyInitials, scoreStars } from '@/models/lead'
import { CADENCE_STATUS } from '@/models/enums'
import { paths } from '@/routes/paths'
import { leadsService } from '@/services/leads.service'
import styles from './LeadTable.module.css'

export function LeadTable({ items, loading, error, compact = false, onRetry, onClearFilters, onDeleted }) {
  const navigate = useNavigate()
  const [deleteId, setDeleteId] = useState(null)

  if (loading) return <Skeleton lines={4} />
  if (error) return <ErrorState message={error} onRetry={onRetry} />
  if (items.length === 0) {
    return (
      <EmptyState
        title="Nenhuma empresa encontrada"
        description="Ajuste a busca ou os filtros para encontrar leads na sua carteira."
        actionLabel={onClearFilters ? 'Limpar filtros' : undefined}
        onAction={onClearFilters}
      />
    )
  }

  const rows = compact ? items.slice(0, 4) : items

  const handleDelete = async (e, leadId) => {
    e.stopPropagation()
    setDeleteId(leadId)
  }

  const confirmDelete = async () => {
    const leadId = deleteId
    try {
      await leadsService.remove(leadId)
      window.dispatchEvent(new Event('leadway:leads-changed'))
      setDeleteId(null)
      if (typeof onDeleted === 'function') {
        onDeleted(leadId)
      } else if (typeof onRetry === 'function') {
        onRetry()
      }
    } catch (err) {
      throw new Error(err?.message || 'Não foi possível excluir o lead.')
    }
  }

  return (
    <><div className={styles.scroll}>
      <div className={[styles.table, compact ? styles.compact : ''].filter(Boolean).join(' ')}>
        <div className={[styles.row, styles.head].join(' ')}>
          <span>Empresa</span>
          <span>Fase do funil</span>
          <span>Responsável</span>
          <span>Prioridade</span>
          <span>Cadência</span>
          <span>Último contato</span>
          <span />
        </div>

        {rows.map((lead) => {
          const stars = scoreStars(lead.score)
          const cadence = CADENCE_STATUS[lead.cadenceStatus] ?? CADENCE_STATUS.UNTRACKED
          const priority = String(lead.prioridade ?? '').toUpperCase()
          const priorityTone = priority === 'ALTA' || priority === 'ALTO' ? 'danger' : priority === 'MEDIA' || priority === 'MÉDIA' || priority === 'MEDIO' ? 'warning' : priority === 'BAIXA' || priority === 'BAIXO' ? 'success' : 'neutral'
          const priorityLabel = priorityTone === 'danger' ? 'Alta' : priorityTone === 'warning' ? 'Média' : priorityTone === 'success' ? 'Baixa' : 'Não definida'
          return (
            <div className={styles.row} key={lead.id}>
              <button
                type="button"
                className={styles.companyCell}
                onClick={() => navigate(`${paths.leads}/${lead.id}`)}
              >
                <span className={styles.companyIcon}>{companyInitials(lead.name)}</span>
                <span className={styles.companyText}>
                  <strong>{lead.name}</strong>
                  <small>
                    <span className={styles.potential}>Potencial · </span>{stars.filled}
                    <i>{stars.empty}</i>
                  </small>
                </span>
              </button>
              <span>
                <Badge tone="neutral">{lead.stage}</Badge>
              </span>
              <span>{lead.representativeName}</span>
              <span><Badge tone={priorityTone}>{priorityLabel}</Badge></span>
              <span><Badge tone={cadence.tone}>{cadence.label}</Badge></span>
              <span>{lead.lastContact || '—'}</span>
              <span className={styles.more}>
                {lead.id != null && <Button
                  kind="ghost"
                  size="sm"
                  aria-label="Excluir lead"
                  title="Excluir lead"
                  onClick={(e) => handleDelete(e, lead.id)}
                >
                  <Icon name="trash" size={16} />
                </Button>}
              </span>
            </div>
          )
        })}
      </div>
    </div>
    {deleteId != null && <ConfirmActionModal title="Excluir lead?" description="O registro será removido. Esta ação não poderá ser desfeita." onClose={() => setDeleteId(null)} onConfirm={confirmDelete} />}
    </>
  )
}

export default LeadTable
