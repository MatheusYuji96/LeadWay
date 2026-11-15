import { Badge } from '@/components/common/Badge'
import { EmptyState } from '@/components/common/Feedback'
import { Icon } from '@/components/common/Icon'
import styles from './Timeline.module.css'

const iconToneClass = [styles.iconTone0, styles.iconTone1, styles.iconTone2]

export function Timeline({ items = [], extended = false, onDelete }) {
  if (items.length === 0) {
    return (
      <EmptyState
        icon="clock"
        title="Nenhuma interação registrada"
        description="Quando você registrar contatos e anotações, elas aparecem aqui."
      />
    )
  }

  const visible = extended ? items : items.slice(0, 2)

  return (
    <div className={styles.timeline}>
      {visible.map((item, index) => (
        <div className={styles.item} key={item.id ?? `${item.title}-${index}`}>
          <span className={[styles.icon, iconToneClass[index % 3]].join(' ')}>
            <Icon name={item.icon ?? 'phone'} />
          </span>
          <div className={styles.content}>
            <small>{item.dateLabel}</small>
            <strong>{item.title}</strong>
            <span>{item.author}</span>
            <p>{item.description}</p>
          </div>
          {onDelete && item.id != null && <button type="button" aria-label={`Excluir interação ${item.title}`} title="Excluir interação" onClick={() => onDelete(item.id)} style={{ border: 'none', background: 'transparent', cursor: 'pointer', color: '#a33', padding: 8 }}><Icon name="trash" size={16} /></button>}
          <Badge tone={item.status === 'pending' ? 'success' : 'neutral'}>
            {item.status === 'pending' ? 'Retorno' : 'Concluído'}
          </Badge>
        </div>
      ))}
    </div>
  )
}

export default Timeline
