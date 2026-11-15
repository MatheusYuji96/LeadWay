import { Icon } from './Icon'
import { Button } from './Button'
import styles from './Feedback.module.css'

export function Spinner({ label = 'Carregando...' }) {
  return (
    <div className={styles.spinnerWrap} role="status">
      <span className={styles.spinner} />
      <span>{label}</span>
    </div>
  )
}

export function Skeleton({ lines = 3, cards = 0 }) {
  return (
    <div className={styles.skeletonWrap} aria-hidden="true">
      {cards > 0 && (
        <div className={styles.skeletonRow}>
          {Array.from({ length: cards }, (_, i) => (
            <i key={i} />
          ))}
        </div>
      )}
      <div className={[styles.skeleton, styles.skTitle].join(' ')} />
      {Array.from({ length: lines }, (_, i) => (
        <div key={i} className={[styles.skeleton, styles.skLine, i === lines - 1 ? styles.skShort : ''].filter(Boolean).join(' ')} />
      ))}
    </div>
  )
}

export function EmptyState({ icon = 'briefcase', title, description, actionLabel, onAction }) {
  return (
    <div className={styles.empty}>
      <span className={styles.emptyIcon}>
        <Icon name={icon} size={24} />
      </span>
      <h3>{title}</h3>
      {description && <p>{description}</p>}
      {actionLabel && (
        <Button kind="primary" icon="plus" onClick={onAction}>
          {actionLabel}
        </Button>
      )}
    </div>
  )
}

export function InlineMessage({ tone = 'error', title, description, children }) {
  return (
    <div className={[styles.message, styles[tone] ?? styles.error].join(' ')} role={tone === 'error' ? 'alert' : 'status'}>
      <Icon name={tone === 'success' ? 'check' : tone === 'warning' ? 'bell' : 'close'} />
      <div>
        <strong>{title}</strong>
        {description && <span>{description}</span>}
        {children}
      </div>
    </div>
  )
}

export function ErrorState({ message, onRetry }) {
  return (
    <div className={styles.errorState}>
      <span className={styles.errorIcon}>
        <Icon name="close" size={22} />
      </span>
      <strong>Não foi possível carregar</strong>
      <p>{message || 'Verifique sua conexão e tente novamente.'}</p>
      {onRetry && (
        <Button onClick={onRetry} icon="arrow">
          Tentar novamente
        </Button>
      )}
    </div>
  )
}
