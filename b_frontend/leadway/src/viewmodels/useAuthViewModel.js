import { useCallback, useState } from 'react'
import { useSession } from './sessionContext'
import { firstError, isEmail, isRequired, passwordRules } from './validators'

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
    if (mode === 'signup' && !isRequired(values.whatsapp)) next.whatsapp = 'Informe um WhatsApp para contato.'
    if (!isEmail(values.email)) next.email = 'Informe um e-mail válido.'
    if (!isRequired(values.password)) next.password = 'Informe sua senha.'
    if (mode === 'signup') {
      if (!passwordRules(values.password).valid) next.password = 'Use ao menos 8 caracteres, uma maiúscula e um número.'
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
