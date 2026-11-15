import styles from './MiniStats.module.css'

export function MiniStats({ items = [] }) {
  if (items.length === 0) return null
  return (
    <div className={styles.wrap}>
      {items.map((item) => (
        <span key={item.label} className={item.tone === 'danger' ? styles.danger : ''}>
          <small>{item.label}</small>
          <strong>{item.value}</strong>
        </span>
      ))}
    </div>
  )
}

export default MiniStats
