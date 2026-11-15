import styles from './Panel.module.css'

export function Panel({ children, className = '', ...rest }) {
  return (
    <section className={[styles.panel, className].filter(Boolean).join(' ')} {...rest}>
      {children}
    </section>
  )
}

export function PanelHead({ title, subtitle, actions, className = '' }) {
  return (
    <div className={[styles.head, className].filter(Boolean).join(' ')}>
      <div className={styles.headText}>
        <h2 className={styles.title}>{title}</h2>
        {subtitle && <p>{subtitle}</p>}
      </div>
      {actions && <div className={styles.headActions}>{actions}</div>}
    </div>
  )
}

export default Panel
