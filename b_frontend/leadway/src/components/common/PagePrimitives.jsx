import { Icon } from './Icon'
import styles from './PagePrimitives.module.css'

export function PageHeading({ eyebrow, title, description, actions }) {
  return (
    <div className={styles.heading}>
      <div>
        {eyebrow && <div className={styles.eyebrow}>{eyebrow}</div>}
        <h1 className={styles.headingTitle}>{title}</h1>
        {description && <p>{description}</p>}
      </div>
      {actions && <div className={styles.headingActions}>{actions}</div>}
    </div>
  )
}

export function Breadcrumb({ items, onNavigate }) {
  return (
    <nav className={styles.breadcrumb} aria-label="Trilha de navegação">
      {items.map((item, index) => {
        const isLast = index === items.length - 1
        const clickable = !isLast && onNavigate
        return (
          <span key={`${item.label}-${index}`} className={styles.crumb}>
            {clickable ? (
              <button type="button" onClick={() => onNavigate(item.to)}>
                {item.label}
              </button>
            ) : (
              <strong>{item.label}</strong>
            )}
            {!isLast && <Icon name="chevron" size={13} />}
          </span>
        )
      })}
    </nav>
  )
}

export function Tabs({ tabs, active, onChange }) {
  return (
    <div className={styles.tabs} role="tablist">
      {tabs.map((tab) => (
        <button
          key={tab.value}
          type="button"
          role="tab"
          aria-selected={active === tab.value}
          className={active === tab.value ? styles.tabActive : ''}
          onClick={() => onChange(tab.value)}
        >
          {tab.label}
        </button>
      ))}
    </div>
  )
}
