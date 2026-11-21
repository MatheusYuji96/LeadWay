// Fonte única para os indicadores do detalhe e da listagem.
export const parseCadenceDate = value => {
  if (!value) return null
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? null : date
}
export function cadenceProgress(item, now = new Date()) {
  if (!item) return { key: 'UNTRACKED', label: 'Não monitorada', tone: 'neutral', progress: 0, remaining: '—' }
  const start = parseCadenceDate(item.dataInicio) ?? parseCadenceDate(item.criadoEm)
  const end = parseCadenceDate(item.dataFim)
  if (!end) return { key: 'UNTRACKED', label: 'Sem prazo', tone: 'neutral', progress: 0, remaining: 'Prazo não definido' }
  const diff = end.getTime() - now.getTime()
  const dayStart = new Date(end.getFullYear(), end.getMonth(), end.getDate()).getTime()
  const todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime()
  const key = diff < 0 ? 'LATE' : todayStart >= dayStart ? 'ATTENTION' : 'ON_TRACK'
  const info = { LATE: ['Atrasada', 'danger'], ATTENTION: ['Atenção', 'warning'], ON_TRACK: ['Em dia', 'success'] }[key]
  const total = start && end.getTime() > start.getTime() ? end.getTime() - start.getTime() : 0
  const progress = total ? Math.min(100, Math.max(0, ((now.getTime() - start.getTime()) / total) * 100)) : diff < 0 ? 100 : 0
  const abs = Math.max(0, diff)
  const days = Math.floor(abs / 86400000)
  const hours = Math.floor(abs % 86400000 / 3600000)
  const minutes = Math.floor(abs % 3600000 / 60000)
  return { key, label: info[0], tone: info[1], progress, remaining: diff < 0 ? `Vencida há ${Math.floor(-diff / 86400000)}d ${Math.floor(-diff % 86400000 / 3600000)}h ${Math.floor(-diff % 3600000 / 60000)}min` : `${days}d ${hours}h ${minutes}min`, end, start }
}
export function currentCadence(items) {
  const candidates = (items ?? []).filter(item => ['ATIVA', 'PAUSADA'].includes(item.status))
  return [...candidates].sort((a,b) => (parseCadenceDate(b.criadoEm)?.getTime() ?? b.id ?? 0) - (parseCadenceDate(a.criadoEm)?.getTime() ?? a.id ?? 0))[0] ?? null
}
