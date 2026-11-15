/**
 * @typedef {Object} Representative
 * @property {string} id
 * @property {string} name
 * @property {string} code Código do vendedor (VEN-0001).
 * @property {string} region
 * @property {string} email
 * @property {string} [phone]
 * @property {string} initials
 * @property {number} leads Quantidade de leads na carteira.
 * @property {number} pending Leads sem atualização.
 * @property {number} contacts Contatos realizados.
 * @property {string} [address]
 * @property {string} [since] Data de entrada na equipe (ISO).
 * @property {boolean} active
 */

/**
 * Iniciais a partir do nome completo.
 * @param {string} name
 */
export function repInitials(name = '') {
  const parts = name.trim().split(/\s+/).filter(Boolean)
  if (parts.length === 0) return '?'
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase()
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
}

/**
 * E-mail derivado do nome, usado apenas como rótulo de exibição.
 * @param {string} name
 * @param {string} domain
 */
export function repDisplayEmail(name = '', domain = 'leadway.com') {
  return `${name.toLowerCase().replace(/\s+/g, '.')}@${domain}`
}
