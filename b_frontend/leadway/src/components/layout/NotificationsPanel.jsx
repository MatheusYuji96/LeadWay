import { Button } from '@/components/common/Button'
import { Badge } from '@/components/common/Badge'
import { ErrorState, Skeleton } from '@/components/common/Feedback'
import { Icon } from '@/components/common/Icon'
import { Title } from '@/components/common/Title'
import styles from './NotificationsPanel.module.css'

export function NotificationsPanel({ items, loading, error, onClose, reload }) {
  return (
    <div className={styles.panel} role="dialog" aria-label="Notificações">
      <div className={styles.head}>
        <Title level={2}>Notificações</Title>
        <div className={styles.headRight}>
          {items.length > 0 && <Badge tone="brand">{items.length} novas</Badge>}
          <Button kind="ghost" onClick={onClose} aria-label="Fechar notificações">
            <Icon name="close" size={16} />
          </Button>
        </div>
      </div>

      {loading && <Skeleton lines={4} />}
      {!loading && error && <ErrorState message={error} onRetry={reload} />}
      {!loading && !error && items.length === 0 && (
        <p className={styles.empty}>Você não tem notificações no momento.</p>
      )}

      {!loading &&
        !error &&
        items.map((item) => (
          <div className={styles.item} key={item.id}>
            <span className={[styles.itemIcon, styles[item.tone] ?? ''].join(' ')}>
              <Icon name={item.icon} />
            </span>
            <div>
              <strong>{item.title}</strong>
              <p>{item.description}</p>
              <small>{item.createdAt}</small>
            </div>
          </div>
        ))}
    </div>
  )
}

export default NotificationsPanel
