import { FUNNEL_STAGES } from './enums'

/**
 * @typedef {Object} Lead
 * @property {string} id
 * @property {string} name
 * @property {string} cnpj
 * @property {string} sector
 * @property {number} score Potencial de 1 a 5.
 * @property {string} stage Etapa do funil (ver FUNNEL_STAGES).
 * @property {string} representativeId
 * @property {string} representativeName
 * @property {string} lastContact Texto exibido ("Hoje, 09:30", "Há 8 dias"...).
 * @property {string} nextAction Texto exibido ("Reunião • amanhã").
 * @property {string} cadenceStatus Chave de CADENCE_STATUS.
 * @property {{ street: string, number: string, complement: string, neighborhood: string, city: string, state: string, zipCode: string }} [address]
 * @property {string} [phone]
 * @property {string} [whatsapp]
 * @property {string} [email]
 * @property {string} [enteredStageAt] Data de entrada na etapa atual (ISO).
 * @property {number} [daysInStage]
 */

/**
 * Iniciais usadas no ícone da empresa (ex.: "TechNova Sistemas" -> "TN").
 * @param {string} name
 */
export function companyInitials(name = '') {
  const parts = name.trim().split(/\s+/).filter(Boolean)
  if (parts.length === 0) return '?'
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase()
  return (parts[0][0] + parts[1][0]).toUpperCase()
}

/**
 * Sequência de estrelas preenchidas/vazias para o score do lead.
 * @param {number} score
 */
export function scoreStars(score = 0) {
  const filled = Math.max(0, Math.min(5, Math.round(score)))
  return { filled: '★'.repeat(filled), empty: '★'.repeat(5 - filled) }
}

/**
 * Valida se uma string representa uma etapa válida do funil.
 * @param {string} stage
 */
export function isValidStage(stage) {
  return FUNNEL_STAGES.includes(stage)
}

/**
 * Rótulo amigável de uma etapa inexistente/ausente.
 * @param {string} [stage]
 */
export function stageLabel(stage) {
  return isValidStage(stage) ? stage : 'Não definida'
}
