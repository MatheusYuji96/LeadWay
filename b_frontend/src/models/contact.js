/**
 * @typedef {Object} Contact
 * @property {string} id
 * @property {string} name
 * @property {string} role Cargo.
 * @property {string} email
 * @property {string} whatsapp
 * @property {string} companyId
 * @property {string} companyName
 * @property {string} initials
 * @property {'active' | 'pending'} status
 */

/**
 * Iniciais do nome do contato.
 * @param {string} name
 */
export function contactInitials(name = '') {
  const parts = name.trim().split(/\s+/).filter(Boolean)
  if (parts.length === 0) return '?'
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase()
  return (parts[0][0] + parts[1][0]).toUpperCase()
}

/**
 * Rótulo do status do contato.
 * @param {Contact['status']} status
 */
export function contactStatusLabel(status) {
  return status === 'pending' ? 'Retorno pendente' : 'Ativo'
}

/**
 * Tom do badge conforme o status.
 * @param {Contact['status']} status
 */
export function contactStatusTone(status) {
  return status === 'pending' ? 'warning' : 'success'
}
