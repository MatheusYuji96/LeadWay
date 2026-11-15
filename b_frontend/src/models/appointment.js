/**
 * @typedef {Object} Appointment
 * @property {string} id
 * @property {string} date Data ISO (yyyy-mm-dd).
 * @property {string} startTime HH:mm.
 * @property {string} [endTime] HH:mm.
 * @property {string} title
 * @property {string} type Contato | Visita | Reunião | Follow-up | Orçamento.
 * @property {string} companyId
 * @property {string} companyName
 * @property {string} [contactName]
 * @property {string} [detail]
 * @property {string} [location]
 * @property {'brand'|'blue'|'purple'|'warning'} tone
 * @property {string} representativeId
 */

/**
 * Converte um horário 24h para o formato solicitado (12h/24h).
 * @param {string} time HH:mm
 * @param {'12h'|'24h'} format
 */
export function formatTime(time, format = '24h') {
  if (!time) return ''
  if (format === '24h') return time
  const [hour, minute] = time.split(':').map(Number)
  const suffix = hour >= 12 ? 'PM' : 'AM'
  return `${hour % 12 || 12}:${String(minute).padStart(2, '0')} ${suffix}`
}

/**
 * Duração em minutos entre início e fim.
 * @param {string} startTime
 * @param {string} [endTime]
 * @returns {number}
 */
export function durationMinutes(startTime, endTime) {
  if (!startTime || !endTime) return 0
  const [sh, sm] = startTime.split(':').map(Number)
  const [eh, em] = endTime.split(':').map(Number)
  return eh * 60 + em - (sh * 60 + sm)
}

/**
 * Rótulo legível da duração ("45 min", "1h", "1h30").
 * @param {number} minutes
 */
export function durationLabel(minutes) {
  if (!minutes || minutes <= 0) return ''
  const hours = Math.floor(minutes / 60)
  const rest = minutes % 60
  if (hours === 0) return `${rest} min`
  if (rest === 0) return `${hours}h`
  return `${hours}h${String(rest).padStart(2, '0')}`
}

/**
 * Tom do badge de um compromisso.
 * @param {Appointment['tone']} tone
 */
export function appointmentBadgeTone(tone) {
  if (tone === 'brand') return 'brand'
  if (tone === 'warning') return 'warning'
  return 'info'
}
