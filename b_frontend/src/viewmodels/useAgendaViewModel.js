import { useCallback, useEffect, useMemo, useState } from 'react'
import { agendaService } from '@/services/agenda.service'
import { isMockMode } from '@/services/http'
import { useAsync } from './useAsync'
import { useSession } from './sessionContext'

// No modo demonstração os dados são fixos em 15/05/2025; com a API real vale a data de hoje.
const REFERENCE_TODAY = isMockMode ? new Date(2025, 4, 15) : new Date()

function toKey(date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

function monthKey(date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`
}

/**
 * ViewModel da agenda comercial: visões mês/semana/dia, dia selecionado,
 * formato de horário, conexão com o Cal.com e criação/cancelamento de compromissos.
 * Os compromissos vêm do Cal.com (via backend), não do banco do LeadWay.
 */
export function useAgendaViewModel() {
  const { user } = useSession()
  const vendedorId = user?.id ?? user?.vendedorId ?? user?.vendedor?.id
  const [view, setView] = useState('Mês')
  const [cursor, setCursor] = useState(new Date(REFERENCE_TODAY.getFullYear(), REFERENCE_TODAY.getMonth(), 1))
  const [selectedDate, setSelectedDate] = useState(toKey(REFERENCE_TODAY))
  const [timeFormat, setTimeFormat] = useState('24h')
  const [selectedTime, setSelectedTime] = useState('09:00')
  const [eventModalOpen, setEventModalOpen] = useState(false)
  const { data, loading, error, execute } = useAsync()
  const exec = execute

  const load = useCallback(
    () => exec(() => agendaService.list({ month: monthKey(cursor), vendedorId })),
    [exec, cursor, vendedorId],
  )

  useEffect(() => {
    load()
  }, [load])

  // `connected` só é false quando o backend diz que o vendedor não conectou o Cal.com.
  const ready = data != null
  const connected = data?.connected !== false

  // Horários livres do dia selecionado (consulta a disponibilidade real no Cal.com).
  const slotsAsync = useAsync()
  const execSlots = slotsAsync.execute
  const isPastDay = selectedDate < toKey(REFERENCE_TODAY)
  const loadSlots = useCallback(
    () => execSlots(() => agendaService.slots({ vendedorId, date: selectedDate })),
    [execSlots, vendedorId, selectedDate],
  )

  useEffect(() => {
    if (ready && connected && !isPastDay) loadSlots()
  }, [ready, connected, isPastDay, loadSlots])

  const freeSlots = useMemo(
    () => (connected && !isPastDay && slotsAsync.data?.date === selectedDate ? (slotsAsync.data.slots ?? []).map((slot) => slot.label) : []),
    [connected, isPastDay, slotsAsync.data, selectedDate],
  )

  const appointments = useMemo(() => data?.items ?? [], [data])
  const overview = data?.overview ?? []

  const monthLabel = useMemo(
    () =>
      cursor.toLocaleDateString('pt-BR', { month: 'long', year: 'numeric' })
        .replace(/^./, (c) => c.toUpperCase()),
    [cursor],
  )

  const appointmentsByDay = useMemo(() => {
    const map = new Map()
    for (const appt of appointments) {
      const list = map.get(appt.date) ?? []
      list.push(appt)
      map.set(appt.date, list)
    }
    for (const list of map.values()) list.sort((a, b) => a.startTime.localeCompare(b.startTime))
    return map
  }, [appointments])

  const selectedDayAppointments = useMemo(
    () => appointmentsByDay.get(selectedDate) ?? [],
    [appointmentsByDay, selectedDate],
  )

  const weekDays = useMemo(() => {
    const [year, month, day] = selectedDate.split('-').map(Number)
    const current = new Date(year, month - 1, day)
    const offset = current.getDay()
    const start = new Date(year, month - 1, day - offset)
    return Array.from({ length: 7 }, (_, i) => {
      const date = new Date(start.getFullYear(), start.getMonth(), start.getDate() + i)
      return {
        key: toKey(date),
        weekday: date.toLocaleDateString('pt-BR', { weekday: 'short' }).replace('.', '').toUpperCase(),
        dayNumber: date.getDate(),
        isCurrent: toKey(date) === selectedDate,
        isToday: toKey(date) === toKey(REFERENCE_TODAY),
      }
    })
  }, [selectedDate])

  const monthCells = useMemo(() => {
    const year = cursor.getFullYear()
    const month = cursor.getMonth()
    const firstWeekday = new Date(year, month, 1).getDay()
    const daysInMonth = new Date(year, month + 1, 0).getDate()
    const cellCount = Math.ceil((firstWeekday + daysInMonth) / 7) * 7
    return Array.from({ length: cellCount }, (_, i) => {
      const dayNumber = i - firstWeekday + 1
      const inMonth = dayNumber >= 1 && dayNumber <= daysInMonth
      const date = new Date(year, month, dayNumber)
      const key = toKey(date)
      return {
        key,
        dayNumber: date.getDate(),
        inMonth,
        isToday: key === toKey(REFERENCE_TODAY),
        isSelected: key === selectedDate,
        appointments: inMonth ? appointmentsByDay.get(key) ?? [] : [],
        hasActivity: inMonth && (appointmentsByDay.get(key)?.length ?? 0) > 0,
      }
    })
  }, [appointmentsByDay, cursor, selectedDate])

  const hours = useMemo(() => Array.from({ length: 11 }, (_, i) => i + 8), [])

  const dayEventsByHour = useMemo(() => {
    const map = new Map()
    for (const appt of selectedDayAppointments) {
      const hour = Number(appt.startTime.split(':')[0])
      const list = map.get(hour) ?? []
      list.push(appt)
      map.set(hour, list)
    }
    return map
  }, [selectedDayAppointments])

  const selectedWeekdayLabel = useMemo(() => {
    const [year, month, day] = selectedDate.split('-').map(Number)
    return new Date(year, month - 1, day).toLocaleDateString('pt-BR', { weekday: 'long' })
  }, [selectedDate])

  const selectedDayNumber = Number(selectedDate.split('-')[2])

  const goToPrevMonth = useCallback(() => {
    setCursor((prev) => new Date(prev.getFullYear(), prev.getMonth() - 1, 1))
  }, [])

  const goToNextMonth = useCallback(() => {
    setCursor((prev) => new Date(prev.getFullYear(), prev.getMonth() + 1, 1))
  }, [])

  const goToday = useCallback(() => {
    setCursor(new Date(REFERENCE_TODAY.getFullYear(), REFERENCE_TODAY.getMonth(), 1))
    setSelectedDate(toKey(REFERENCE_TODAY))
  }, [])

  const createAppointment = useCallback(
    async (payload) => {
      await agendaService.create({ ...payload, vendedorId })
      await Promise.all([load(), loadSlots()])
      return true
    },
    [load, loadSlots, vendedorId],
  )

  const cancelAppointment = useCallback(
    async (id) => {
      await agendaService.cancel(id, { vendedorId })
      await Promise.all([load(), loadSlots()])
    },
    [load, loadSlots, vendedorId],
  )

  const connectCal = useCallback(
    async (apiKey) => {
      await agendaService.connect({ vendedorId, apiKey })
      await load()
    },
    [load, vendedorId],
  )

  const disconnectCal = useCallback(async () => {
    await agendaService.disconnect(vendedorId)
    await load()
  }, [load, vendedorId])

  const periodLabel =
    view === 'Mês'
      ? monthLabel
      : view === 'Semana'
        ? (() => {
            const first = weekDays[0].key
            const last = weekDays[6].key
            const f = new Date(`${first}T00:00:00`)
            const l = new Date(`${last}T00:00:00`)
            const sameMonth = f.getMonth() === l.getMonth()
            return sameMonth
              ? `${f.getDate()} — ${l.getDate()} de ${f.toLocaleDateString('pt-BR', { month: 'long', year: 'numeric' }).replace(/^./, (c) => c.toUpperCase())}`
              : `${f.toLocaleDateString('pt-BR', { day: 'numeric', month: 'short' })} — ${l.toLocaleDateString('pt-BR', { day: 'numeric', month: 'short', year: 'numeric' })}`
          })()
        : new Date(`${selectedDate}T00:00:00`).toLocaleDateString('pt-BR', {
            weekday: 'long',
            day: 'numeric',
            month: 'long',
          }).replace(/^./, (c) => c.toUpperCase())

  return {
    view,
    setView,
    timeFormat,
    setTimeFormat,
    selectedTime,
    setSelectedTime,
    selectedDate,
    selectedDayNumber,
    selectedWeekdayLabel,
    selectedDayAppointments,
    monthCells,
    weekDays,
    hours,
    dayEventsByHour,
    appointments,
    overview,
    freeSlots,
    periodLabel,
    loading,
    error,
    vendedorId,
    connected,
    slotsLoading: slotsAsync.loading,
    eventModalOpen,
    openEventModal: () => setEventModalOpen(true),
    closeEventModal: () => setEventModalOpen(false),
    selectDay: setSelectedDate,
    goToPrevMonth,
    goToNextMonth,
    goToday,
    createAppointment,
    cancelAppointment,
    connectCal,
    disconnectCal,
    reload: load,
    referenceToday: toKey(REFERENCE_TODAY),
  }
}
