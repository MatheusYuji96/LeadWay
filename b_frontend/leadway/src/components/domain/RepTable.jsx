import { useNavigate } from 'react-router-dom'
import { Avatar } from '@/components/common/Avatar'
import { Badge } from '@/components/common/Badge'
import { Button } from '@/components/common/Button'
import { EmptyState, ErrorState, Skeleton } from '@/components/common/Feedback'
import { Icon } from '@/components/common/Icon'
import { repDisplayEmail } from '@/models/representative'
import { paths } from '@/routes/paths'
import { representativesService } from '@/services/representatives.service'
import styles from './RepTable.module.css'

export function RepTable({ items, loading, error, onRetry, onClearFilters, onDeleted }) {
  const navigate = useNavigate()

  if (loading) return <Skeleton lines={4} />
  if (error) return <ErrorState message={error} onRetry={onRetry} />
  if (items.length === 0) {
    return (
      <EmptyState
        icon="users"
        title="Nenhum representante encontrado"
        description="Ajuste a busca para encontrar alguém da sua equipe."
        actionLabel="Limpar filtros"
        onAction={onClearFilters}
      />
    )
  }

  const handleDelete = async (e, repId) => {
    e.stopPropagation()
    if (!window.confirm('Tem certeza que deseja excluir este representante?')) {
      return
    }
    try {
      await representativesService.remove(repId)
      if (typeof onDeleted === 'function') {
        onDeleted(repId)
      } else if (typeof onRetry === 'function') {
        onRetry()
      }
    } catch (err) {
      alert(err?.message || 'Não foi possível excluir o representante.')
    }
  }

  return (
    <div className={styles.scroll}>
      <div className={styles.table}>
        <div className={[styles.row, styles.head].join(' ')}>
          <span>Representante</span>
          <span>Código</span>
          <span>Região</span>
          <span>Leads</span>
          <span>Contatos</span>
          <span>Pendentes</span>
          <span>Status</span>
          <span />
        </div>

        {items.map((rep) => (
          <div className={styles.row} key={rep.id}>
            <button type="button" className={styles.personCell} onClick={() => navigate(`${paths.team}/${rep.id}`)}>
              <Avatar initials={rep.initials} />
              <span className={styles.personText}>
                <strong>{rep.name}</strong>
                <small>{repDisplayEmail(rep.name)}</small>
              </span>
            </button>
            <span>{rep.code}</span>
            <span>{rep.region}</span>
            <span className={styles.strong}>{rep.leads}</span>
            <span>{rep.contacts}</span>
            <span>
              <Badge tone={rep.pending > 4 ? 'danger' : 'warning'}>{rep.pending}</Badge>
            </span>
            <span>{rep.active ? <Badge tone="success">Ativo</Badge> : <Badge tone="neutral">Inativo</Badge>}</span>
            <span className={styles.more}>
              <Button
                kind="ghost"
                size="sm"
                aria-label="Excluir representante"
                title="Excluir representante"
                onClick={(e) => handleDelete(e, rep.id)}
              >
                <Icon name="trash" size={16} />
              </Button>
            </span>
          </div>
        ))}
      </div>
    </div>
  )
}

export default RepTable
