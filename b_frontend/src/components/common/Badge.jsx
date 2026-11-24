import styles from './Badge.module.css'

export function Badge({ children, tone = 'neutral', className = '' }) {
  return (
    <span className={[styles.badge, styles[tone] ?? styles.neutral, className].filter(Boolean).join(' ')}>
      <span className={styles.dot} />
      {children}
    </span>
  )
}

export default Badge
