import styles from './Avatar.module.css'

const sizes = {
  sm: styles.sm,
  md: '',
  lg: styles.lg,
  xlg: styles.xlg,
}

export function Avatar({ children, initials, size = 'md', className = '' }) {
  return (
    <span
      className={[styles.avatar, sizes[size] ?? '', className].filter(Boolean).join(' ')}
      aria-hidden="true"
    >
      {initials ?? children}
    </span>
  )
}

export default Avatar
