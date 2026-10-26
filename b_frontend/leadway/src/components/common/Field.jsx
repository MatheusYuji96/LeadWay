import { Icon } from './Icon'
import styles from './Field.module.css'

export function Field({
  label,
  placeholder,
  type = 'text',
  icon,
  value,
  defaultValue,
  onChange,
  error,
  required = false,
  className = '',
  name,
  autoComplete,
}) {
  const input =
    onChange !== undefined ? (
      <input
        type={type}
        name={name}
        placeholder={placeholder}
        value={value ?? ''}
        onChange={(event) => onChange(event.target.value)}
        required={required}
        autoComplete={autoComplete}
      />
    ) : (
      <input
        type={type}
        name={name}
        placeholder={placeholder}
        defaultValue={defaultValue}
        required={required}
        autoComplete={autoComplete}
      />
    )

  return (
    <label className={[styles.field, error ? styles.hasError : '', className].filter(Boolean).join(' ')}>
      {label && <span>{label}</span>}
      <span className={styles.inputWrap}>
        {icon && <Icon name={icon} />}
        {input}
      </span>
      {error && <small className={styles.error}>{error}</small>}
    </label>
  )
}

export function SelectField({ label, children, value, defaultValue, onChange, className = '' }) {
  const select =
    onChange !== undefined ? (
      <select value={value ?? ''} onChange={(event) => onChange(event.target.value)}>
        {children}
      </select>
    ) : (
      <select defaultValue={defaultValue}>{children}</select>
    )

  return (
    <label className={[styles.field, className].filter(Boolean).join(' ')}>
      {label && <span>{label}</span>}
      {select}
    </label>
  )
}

export function TextArea({ label, placeholder, value, defaultValue, onChange, rows = 4, className = '' }) {
  return (
    <label className={[styles.field, className].filter(Boolean).join(' ')}>
      {label && <span>{label}</span>}
      {onChange !== undefined ? (
        <textarea
          placeholder={placeholder}
          value={value ?? ''}
          rows={rows}
          onChange={(event) => onChange(event.target.value)}
        />
      ) : (
        <textarea placeholder={placeholder} defaultValue={defaultValue} rows={rows} />
      )}
    </label>
  )
}

export function Checkbox({ label, hint, checked, onChange, large = false }) {
  return (
    <label className={[styles.checkbox, large ? styles.checkboxLarge : ''].filter(Boolean).join(' ')}>
      <input type="checkbox" checked={checked} onChange={(event) => onChange?.(event.target.checked)} />
      <span className={styles.checkboxBox} />
      <span className={styles.checkboxText}>
        {label}
        {hint && <small>{hint}</small>}
      </span>
    </label>
  )
}
