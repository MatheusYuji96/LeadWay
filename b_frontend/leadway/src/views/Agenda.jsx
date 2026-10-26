import { useState } from 'react'
import { Badge } from '@/components/common/Badge'
import { Button } from '@/components/common/Button'
import { ErrorState, Skeleton } from '@/components/common/Feedback'
import { Icon } from '@/components/common/Icon'
import { Panel, PanelHead } from '@/components/common/Panel'
import { PageHeading, Tabs } from '@/components/common/PagePrimitives'
import { NewEventModal } from '@/components/domain/NewEventModal'
import { appointmentBadgeTone, formatTime } from '@/models/appointment'
import { useAgendaViewModel } from '@/viewmodels/useAgendaViewModel'
import styles from './Agenda.module.css'

const VIEW_TABS = [
  { value: 'Mês', label: 'Mês' },
  { value: 'Semana', label: 'Semana' },
  { value: 'Dia', label: 'Dia' },
]

const WEEKDAYS = ['DOM', 'SEG', 'TER', 'QUA', 'QUI', 'SEX', 'SÁB']

export function Agenda() {
  const vm = useAgendaViewModel()
  const [saving, setSaving] = useState(false)
  const [createError, setCreateError] = useState(null)

  const step = (delta) => {
    if (vm.view === 'Mês') {
      if (delta < 0) vm.goToPrevMonth()
      else vm.goToNextMonth()
      return
    }
    const [year, month, day] = vm.selectedDate.split('-').map(Number)
    const offset = vm.view === 'Semana' ? delta * 7 : delta
    const next = new Date(year, month - 1, day + offset)
    const key = `${next.getFullYear()}-${String(next.getMonth() + 1).padStart(2, '0')}-${String(next.getDate()).padStart(2, '0')}`
    vm.selectDay(key)
  }

  const createEvent = async (payload) => {
    setSaving(true)
    setCreateError(null)
    try {
      await vm.createAppointment(payload)
      return true
    } catch (err) {
      setCreateError(err?.message || 'Não foi possível criar o compromisso.')
      return false
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className={styles.page}>
      <PageHeading
        eyebrow="Rotina comercial"
        title="Agenda"
        description="Reuniões, visitas e follow-ups em visões de mês, semana e dia."
        actions={
          <>
            <label className={styles.format}>
              <span>Formato</span>
              <select value={vm.timeFormat} onChange={(event) => vm.setTimeFormat(event.target.value)}>
                <option value="24h">24h</option>
                <option value="12h">12h</option>
              </select>
            </label>
            <Button kind="primary" icon="plus" onClick={vm.openEventModal}>
              Novo compromisso
            </Button>
          </>
        }
      />

      <div className={styles.toolbar}>
        <Tabs tabs={VIEW_TABS} active={vm.view} onChange={vm.setView} />
        <div className={styles.nav}>
          <Button size="sm" aria-label="Anterior" onClick={() => step(-1)}>
            <Icon name="chevron" size={14} className={styles.chevronLeft} />
          </Button>
          <Button size="sm" onClick={vm.goToday}>
            Hoje
          </Button>
          <Button size="sm" aria-label="Próximo" onClick={() => step(1)}>
            <Icon name="chevron" size={14} />
          </Button>
          <strong className={styles.period}>{vm.periodLabel}</strong>
        </div>
      </div>

      {vm.loading && <Skeleton lines={6} cards={3} />}
      {vm.error && <ErrorState message={vm.error} onRetry={vm.reload} />}

      {!vm.loading && !vm.error && (
        <div className={styles.layout}>
          <div className={styles.calendar}>
            <Panel>
              {vm.view === 'Mês' && <MonthView vm={vm} />}
              {(vm.view === 'Semana' || vm.view === 'Dia') && <TimeView vm={vm} />}
            </Panel>
          </div>

          <aside className={styles.side}>
            <div className={styles.overview}>
              {vm.overview.map((item) => (
                <span className={styles.overviewCard} key={item.key}>
                  <i className={styles[`ov-${item.tone}`]}>
                    <Icon name={item.icon} size={14} />
                  </i>
                  <small>{item.label}</small>
                  <strong>{item.value}</strong>
                </span>
              ))}
            </div>

            <Panel>
              <PanelHead
                title={`${vm.selectedWeekdayLabel}, ${vm.selectedDayNumber}`}
                subtitle="Compromissos do dia"
                actions={
                  <Button kind="ghost" size="sm" icon="plus" onClick={vm.openEventModal}>
                    Novo
                  </Button>
                }
              />
              <div className={styles.dayList}>
                {vm.selectedDayAppointments.length === 0 && (
                  <p className={styles.emptyText}>Nenhum compromisso neste dia.</p>
                )}
                {vm.selectedDayAppointments.map((appt) => (
                  <div className={styles.dayItem} key={appt.id}>
                    <span className={styles.dayTime}>
                      {formatTime(appt.startTime, vm.timeFormat)}
                      {appt.endTime && (
                        <small>{formatTime(appt.endTime, vm.timeFormat)}</small>
                      )}
                    </span>
                    <div>
                      <strong>{appt.title}</strong>
                      <span>{appt.companyName}</span>
                      {appt.contactName && <small>{appt.contactName}</small>}
                    </div>
                    <Badge tone={appointmentBadgeTone(appt.tone)}>{appt.type}</Badge>
                  </div>
                ))}
              </div>
            </Panel>

            <Panel>
              <PanelHead title="Horários livres" subtitle="Sugestões para o dia selecionado" />
              <div className={styles.slots}>
                {vm.freeSlots.map((slot) => (
                  <span key={slot}>{formatTime(slot, vm.timeFormat)}</span>
                ))}
              </div>
            </Panel>
          </aside>
        </div>
      )}

      {vm.eventModalOpen && (
        <NewEventModal
          defaultDate={vm.selectedDate}
          saving={saving}
          error={createError}
          onClose={vm.closeEventModal}
          onSubmit={createEvent}
        />
      )}
    </div>
  )
}

function MonthView({ vm }) {
  return (
    <div className={styles.month}>
      <div className={styles.monthHead}>
        {WEEKDAYS.map((day) => (
          <span key={day}>{day}</span>
        ))}
      </div>
      <div className={styles.monthGrid}>
        {vm.monthCells.map((cell) => (
          <button
            type="button"
            key={cell.key}
            className={[
              styles.monthCell,
              cell.inMonth ? '' : styles.outside,
              cell.isToday ? styles.today : '',
              cell.isSelected ? styles.selected : '',
            ]
              .filter(Boolean)
              .join(' ')}
            onClick={() => vm.selectDay(cell.key)}
          >
            <span className={styles.dayNumber}>{cell.dayNumber}</span>
            <span className={styles.chips}>
              {cell.appointments.slice(0, 2).map((appt) => (
                <i key={appt.id} className={styles[`chip-${appt.tone}`]}>
                  {formatTime(appt.startTime, vm.timeFormat)} {appt.type}
                </i>
              ))}
              {cell.appointments.length > 2 && <i className={styles.chipMore}>+{cell.appointments.length - 2}</i>}
            </span>
          </button>
        ))}
      </div>
    </div>
  )
}

function TimeView({ vm }) {
  const days = vm.view === 'Dia' ? vm.weekDays.filter((day) => day.isCurrent) : vm.weekDays

  return (
    <div className={styles.timeView}>
      <div className={[styles.timeHead, vm.view === 'Dia' ? styles.timeHeadDay : ''].filter(Boolean).join(' ')}>
        <span />
        {days.map((day) => (
          <button
            type="button"
            key={day.key}
            className={day.isCurrent ? styles.timeHeadActive : ''}
            onClick={() => vm.selectDay(day.key)}
          >
            <small>{day.weekday}</small>
            <strong className={day.isToday ? styles.headToday : ''}>{day.dayNumber}</strong>
          </button>
        ))}
      </div>

      <div className={styles.timeBody}>
        {vm.hours.map((hour) => (
          <div className={styles.hourRow} key={hour}>
            <span className={styles.hourLabel}>{String(hour).padStart(2, '0')}:00</span>
            <div className={[styles.hourCells, vm.view === 'Dia' ? styles.hourCellsDay : ''].filter(Boolean).join(' ')}>
              {days.map((day) => {
                const events = vm.dayEventsByHour.get(hour) ?? []
                const dayEvents = vm.view === 'Dia' ? events : vm.appointments.filter(
                  (appt) => appt.date === day.key && Number(appt.startTime.split(':')[0]) === hour,
                )
                return (
                  <div className={styles.hourCell} key={day.key}>
                    {dayEvents.map((appt) => (
                      <span className={[styles.event, styles[`event-${appt.tone}`]].join(' ')} key={appt.id}>
                        <strong>{formatTime(appt.startTime, vm.timeFormat)}</strong>
                        {appt.title}
                        <small>{appt.companyName}</small>
                      </span>
                    ))}
                  </div>
                )
              })}
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

export default Agenda
