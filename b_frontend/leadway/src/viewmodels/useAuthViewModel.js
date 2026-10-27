import { useCallback, useState } from 'react'
import { useSession } from './sessionContext'
import { firstError, isEmail, isRequired, passwordRules, isPhone11 } from './validators'

/**
 * ViewModel das telas de login e cadastro.
 * @param {'login'|'signup'} mode
 */
export function useAuthViewModel(mode) {
  const { login, signup } = useSession()
  const [values, setValues] = useState({
    name: '',
    whatsapp: '',
    email: '',
    password: '',
    confirmPassword: '',
  })
  const [errors, setErrors] = useState({})
  const [formError, setFormError] = useState(null)
  const [submitting, setSubmitting] = useState(false)
  const [finished, setFinished] = useState(false)

  const setValue = useCallback((field, value) => {
    setValues((prev) => ({ ...prev, [field]: value }))
    setErrors((prev) => (prev[field] ? { ...prev, [field]: null } : prev))
  }, [])

  const validate = useCallback(() => {
    const next = {}
    if (mode === 'signup' && !isRequired(values.name)) next.name = 'Informe seu nome completo.'
    if (mode === 'signup') {
      if (!isRequired(values.whatsapp)) next.whatsapp = 'Informe um WhatsApp para contato.'
      else if (!isPhone11(values.whatsapp)) next.whatsapp = 'Informe um WhatsApp válido com 11 dígitos (somente números).'
    }
    if (!isEmail(values.email)) next.email = 'Informe um e-mail válido.'
    if (!isRequired(values.password)) next.password = 'Informe sua senha.'
    if (mode === 'signup') {
      const rules = passwordRules(values.password)
      if (!rules.valid) next.password = 'Senha fraca. Use ao menos 8 caracteres, uma maiúscula, um número e um caractere especial. Sem espaços.'
      if (values.password !== values.confirmPassword) next.confirmPassword = 'As senhas não coincidem.'
    }
    setErrors(next)
    return firstError(next)
  }, [mode, values])

  const submit = useCallback(async () => {
    setFormError(null)
    const invalid = validate()
    if (invalid) {
      setFormError(invalid)
      return false
    }
    setSubmitting(true)
    try {
      if (mode === 'login') {
        await login({ email: values.email, password: values.password })
      } else {
        await signup({
          name: values.name,
          whatsapp: values.whatsapp,
          email: values.email,
          password: values.password,
        })
      }
      setFinished(true)
      return true
    } catch (err) {
      setFormError(err?.message || 'Não foi possível concluir. Tente novamente.')
      return false
    } finally {
      setSubmitting(false)
    }
  }, [login, mode, signup, validate, values])

  return {
    values,
    errors,
    formError,
    submitting,
    finished,
    setValue,
    submit,
  }
}
