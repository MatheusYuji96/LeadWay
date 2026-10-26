import styles from './Title.module.css'

export function Title({ children, level = 1, className = '' }) {
  const Tag = `h${Math.min(3, Math.max(1, level))}`
  return <Tag className={[styles.title, styles[`level${level}`], className].filter(Boolean).join(' ')}>{children}</Tag>
}

export default Title
