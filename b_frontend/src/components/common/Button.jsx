import { Icon } from './Icon'
import styles from './Button.module.css'

export function Button({
  children,
  kind = 'secondary',
  icon,
  size,
  full = false,
  className = '',
  type = 'button',
  disabled = false,
  loading = false,
  onClick,
}) {
  const classes = [
    styles.button,
    styles[kind],
    size === 'sm' ? styles.sm : '',
    full ? styles.full : '',
    className,
  ]
    .filter(Boolean)
    .join(' ')

  return (
    <button type={type} className={classes} disabled={disabled || loading} onClick={onClick}>
      {loading && <span className={styles.spinner} aria-hidden="true" />}
      {!loading && icon && <Icon name={icon} />}
      {children}
    </button>
  )
}

export default Button
