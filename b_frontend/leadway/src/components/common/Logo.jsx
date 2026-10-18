import styles from './Logo.module.css'

export function Logo({ compact = false, light = false, onClick }) {
  return (
    <span
      className={[styles.logo, light ? styles.light : ''].filter(Boolean).join(' ')}
      onClick={onClick}
      role={onClick ? 'button' : undefined}
      tabIndex={onClick ? 0 : undefined}
    >
      <span className={styles.mark}>
        <i />
        <i />
        <i />
      </span>
      {!compact && (
        <strong>
          Lead<span>Way</span>
        </strong>
      )}
    </span>
  )
}

export default Logo
