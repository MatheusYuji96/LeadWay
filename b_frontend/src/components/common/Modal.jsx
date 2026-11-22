import { Button } from './Button'
import { Icon } from './Icon'
import { Title } from './Title'
import styles from './Modal.module.css'

export function Modal({ title, children, onClose, footer, size = 'md' }) {
  return (
    <div className={styles.backdrop} onMouseDown={onClose} role="presentation">
      <div
        className={[styles.modal, size === 'lg' ? styles.lg : ''].filter(Boolean).join(' ')}
        onMouseDown={(event) => event.stopPropagation()}
        role="dialog"
        aria-modal="true"
        aria-label={title || 'Diálogo'}
      >
        <div className={styles.head}>
          {title ? <Title level={2}>{title}</Title> : <span />}
          <Button kind="ghost" onClick={onClose} aria-label="Fechar">
            <Icon name="close" />
          </Button>
        </div>
        {children}
        {footer && <div className={styles.actions}>{footer}</div>}
      </div>
    </div>
  )
}

export function SuccessContent({ title, description, actionLabel, onAction }) {
  return (
    <div className={styles.success}>
      <span className={styles.successIcon}>
        <Icon name="check" size={28} />
      </span>
      <Title level={2}>{title}</Title>
      <p>{description}</p>
      <Button kind="primary" onClick={onAction}>
        {actionLabel} <Icon name="arrow" />
      </Button>
    </div>
  )
}

export default Modal
