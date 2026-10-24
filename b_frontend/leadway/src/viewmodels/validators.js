/**
 * Validações compartilhadas usadas pelos ViewModels de formulário.
 */

export function isEmail(value = '') {
  return /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(String(value).trim())
}

export function isRequired(value = '') {
  return String(value ?? '').trim().length > 0
}

export function passwordRules(value = '') {
  return {
    minLength: value.length >= 8,
    hasUppercase: /[A-Z]/.test(value),
    hasNumber: /\d/.test(value),
    get valid() {
      return this.minLength && this.hasUppercase && this.hasNumber
    },
  }
}

/**
 * Valida um mapa de { field: boolean } e retorna a primeira mensagem.
 * @param {Record<string, string>} errors
 */
export function firstError(errors) {
  const keys = Object.keys(errors)
  return keys.length ? errors[keys[0]] : null
}
