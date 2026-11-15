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
  const hasUppercase = /[A-Z]/.test(value)
  const hasNumber = /\d/.test(value)
  const hasSpecial = /[^A-Za-z0-9]/.test(value)
  const noSpaces = !/\s/.test(value)
  const minLength = value.length >= 8
  return {
    minLength,
    hasUppercase,
    hasNumber,
    hasSpecial,
    noSpaces,
    get valid() {
      // Require: min length, uppercase, number, special char and no spaces
      return minLength && hasUppercase && hasNumber && hasSpecial && noSpaces
    },
  }
}

export function isPhone11(value = '') {
  const digits = String(value || '').replace(/\D/g, '')
  return /^\d{11}$/.test(digits)
}

export function isCEP(value = '') {
  const digits = String(value || '').replace(/\D/g, '')
  return /^\d{8}$/.test(digits)
}

// Validação matemática de CNPJ (dígitos verificadores).
export function isCNPJ(value = '') {
  const c = String(value ?? '').replace(/\D/g, '')
  if (!/^\d{14}$/.test(c) || /^(\d)\1{13}$/.test(c)) return false
  const digit = (length) => {
    const weights = length === 12
      ? [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]
      : [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]
    const sum = weights.reduce((acc, weight, i) => acc + Number(c[i]) * weight, 0)
    const rest = sum % 11
    return rest < 2 ? 0 : 11 - rest
  }
  return digit(12) === Number(c[12]) && digit(13) === Number(c[13])
}

/**
 * Valida um mapa de { field: boolean } e retorna a primeira mensagem.
 * @param {Record<string, string>} errors
 */
export function firstError(errors) {
  const keys = Object.keys(errors)
  return keys.length ? errors[keys[0]] : null
}
