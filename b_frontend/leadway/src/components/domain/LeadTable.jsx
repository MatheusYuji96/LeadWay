import { useNavigate } from 'react-router-dom'
import { Badge } from '@/components/common/Badge'
import { Button } from '@/components/common/Button'
import { Icon } from '@/components/common/Icon'
import { EmptyState, ErrorState, Skeleton } from '@/components/common/Feedback'
import { companyInitials, scoreStars } from '@/models/lead'
import { CADENCE_STATUS } from '@/models/enums'
import { paths } from '@/routes/paths'
import { leadsService } from '@/services/leads.service'
import { isMockMode } from '@/services/http'
import styles from './LeadTable.module.css'

export function LeadTable({ items, loading, error, compact = false, onRetry, onClearFilters, onDeleted }) {
  const navigate = useNavigate()

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
    if (!window.confirm('Tem certeza que deseja excluir este lead?')) {
      return
    }
    try {
      await leadsService.remove(leadId)
      window.dispatchEvent(new Event('leadway:leads-changed'))
      if (typeof onDeleted === 'function') {
        onDeleted(leadId)
      } else if (typeof onRetry === 'function') {
        onRetry()
      }
    } catch (err) {
      alert(err?.message || 'Não foi possível excluir o lead.')
    }
  }

  return (
    <div className={styles.scroll}>
      <div className={[styles.table, compact ? styles.compact : ''].filter(Boolean).join(' ')}>
        <div className={[styles.row, styles.head].join(' ')}>
          <span>Empresa</span>
          <span>Fase do funil</span>
          <span>Responsável</span>
          <span>Último contato</span>
          <span>Cadência</span>
          <span>Próxima ação</span>
          <span />
        </div>

        {rows.map((lead) => {
          const stars = scoreStars(lead.score)
          const cadence = CADENCE_STATUS[lead.cadenceStatus] ?? CADENCE_STATUS.ON_TRACK
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
                    {stars.filled}
                    <i>{stars.empty}</i>
                  </small>
                </span>
              </button>
              <span>
                <Badge tone="neutral">{lead.stage}</Badge>
              </span>
              <span>{lead.representativeName}</span>
              <span>{lead.lastContact}</span>
              <span>
                <Badge tone={cadence.tone}>{cadence.label}</Badge>
              </span>
              <span>{lead.nextAction}</span>
              <span className={styles.more}>
                {(isMockMode || /^\d+$/.test(String(lead.id)) && String(lead.id).length < 10) && <Button
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
  )
}

export default LeadTable
