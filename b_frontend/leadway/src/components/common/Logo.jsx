import styles from './Logo.module.css'
import logoSrc from '@/assets/images/LeadWay.svg'

export function Logo({ compact = false, light = false, onClick }) {
  return (
    <span
      className={[styles.logo, light ? styles.light : ''].filter(Boolean).join(' ')}
      onClick={onClick}
      role={onClick ? 'button' : undefined}
      tabIndex={onClick ? 0 : undefined}
    >
      <img
        src={logoSrc}
        alt="LeadWay"
        className={styles.image}
        style={{ width: 'auto', height: '100%', maxHeight: '32px', display: 'block' }}
      />
    </span>
  )
}

export default Logo
