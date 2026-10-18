import { Icon } from './Icon'
import styles from './StatCard.module.css'

export function StatCard({ icon, label, value, detail, delta, tone = 'brand' }) {
  return (
    <div className={styles.card}>
      <div className={[styles.icon, styles[tone] ?? styles.brand].join(' ')}>
        <Icon name={icon} />
      </div>
      <div className={styles.top}>
        <span>{label}</span>
        <Icon name="more" size={14} />
      </div>
      <strong className={styles.value}>{value}</strong>
      <small className={styles.detail}>
        {delta && <b>{delta}</b>} {detail}
      </small>
    </div>
  )
}

export default StatCard
