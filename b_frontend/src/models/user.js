/**
 * @typedef {Object} UserSession
 * @property {string} id
 * @property {string} name
 * @property {string} email
 * @property {string} role
 * @property {string} initials
 */

/**
 * @typedef {Object} UserProfile
 * @property {string} name
 * @property {string} email
 * @property {string} whatsapp
 * @property {string} role Cargo.
 * @property {string} address
 * @property {string} cityState
 * @property {string} initials
 */

/**
 * @typedef {Object} UserPreferences
 * @property {boolean} newLeads
 * @property {boolean} appointmentReminders
 * @property {boolean} lateCadenceAlerts
 * @property {boolean} weeklySummaryEmail
 */

/**
 * Iniciais a partir do nome.
 * @param {string} name
 */
export function userInitials(name = '') {
  const parts = name.trim().split(/\s+/).filter(Boolean)
  if (parts.length === 0) return '?'
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase()
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
}
