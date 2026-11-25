import { useState } from 'react'
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
  readOnly = false,
}) {
  const [passwordVisible, setPasswordVisible] = useState(false)
  const inputType = type === 'password' && passwordVisible ? 'text' : type
  const input =
    onChange !== undefined ? (
      <input
        type={inputType}
        name={name}
        placeholder={placeholder}
        value={value ?? ''}
        onChange={(event) => onChange(event.target.value)}
        required={required}
        autoComplete={autoComplete}
        readOnly={readOnly}
      />
    ) : (
      <input
        type={inputType}
        name={name}
        placeholder={placeholder}
        defaultValue={defaultValue}
        required={required}
        autoComplete={autoComplete}
        readOnly={readOnly}
      />
    )

  return (
    <label className={[styles.field, error ? styles.hasError : '', className].filter(Boolean).join(' ')}>
      {label && <span>{label}</span>}
      <span className={styles.inputWrap}>
        {icon && <Icon name={icon} />}
        {input}
        {type === 'password' && (
          <button type="button" onClick={() => setPasswordVisible((visible) => !visible)}
            aria-label={passwordVisible ? 'Ocultar senha' : 'Mostrar senha'}
            aria-pressed={passwordVisible}
            title={passwordVisible ? 'Ocultar senha' : 'Mostrar senha'}
            style={{ border: 0, background: 'transparent', cursor: 'pointer', display: 'inline-flex', alignItems: 'center', padding: '0 8px', color: 'var(--muted, #64748b)' }}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
              {passwordVisible ? <><path d="M3 3l18 18"/><path d="M10.6 5.2A11 11 0 0 1 12 5c5 0 9.2 4.3 10 7a11.7 11.7 0 0 1-3.1 4.5"/><path d="M6.2 6.2C4.1 7.5 2.7 9.5 2 12c.8 2.7 5 7 10 7 1.4 0 2.8-.3 4-.8"/></> : <><path d="M2 12s3.6-7 10-7 10 7 10 7-3.6 7-10 7S2 12 2 12Z"/><circle cx="12" cy="12" r="3"/></>}
            </svg>
          </button>
        )}
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

export function Checkbox({ label, hint, checked, onChange, large = false, ariaLabel }) {
  return (
    <label className={[styles.checkbox, large ? styles.checkboxLarge : ''].filter(Boolean).join(' ')}>
      <input type="checkbox" role="switch" aria-checked={Boolean(checked)} aria-label={ariaLabel || label || "Ativar ou desativar opção"} checked={Boolean(checked)} onChange={(event) => onChange?.(event.target.checked)} />
      <span className={styles.checkboxBox} />
      <span className={styles.checkboxText}>
        {label}
        {hint && <small>{hint}</small>}
      </span>
    </label>
  )
}
