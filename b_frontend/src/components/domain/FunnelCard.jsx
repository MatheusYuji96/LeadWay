import { useEffect, useState } from 'react'
import { Button } from '@/components/common/Button'
import { Icon } from '@/components/common/Icon'
import { Panel, PanelHead } from '@/components/common/Panel'
import { FUNNEL_STAGES } from '@/models/enums'
import styles from './FunnelCard.module.css'

function Info({ icon, label, value }) {
  return (
    <div className={styles.info}>
      <span>
        <Icon name={icon} />
      </span>
      <div>
        <small>{label}</small>
        <strong>{value}</strong>
      </div>
    </div>
  )
}

function stageElapsed(enteredStageAt, now) {
  if (!enteredStageAt) return '—'
  const start = new Date(enteredStageAt)
  if (Number.isNaN(start.getTime())) return '—'
  const minutes = Math.max(0, Math.floor((now - start.getTime()) / 60000))
  const days = Math.floor(minutes / 1440)
  const hours = Math.floor((minutes % 1440) / 60)
  const mins = minutes % 60
  if (days) return `${days} dia${days !== 1 ? 's' : ''} e ${hours}h`
  if (hours) return `${hours}h ${mins}min`
  return `${mins}min`
}

function stageEntryLabel(value) {
  if (!value) return '—'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '—'
  return date.toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' })
}

export function FunnelCard({ funnel, lead, owner, large = false, onUpdateStage }) {
  const [now, setNow] = useState(Date.now())
  useEffect(() => {
    const timer = setInterval(() => setNow(Date.now()), 30000)
    return () => clearInterval(timer)
  }, [])
  const elapsed = stageElapsed(funnel?.enteredStageAt, now)
  const currentOrder = Number(lead?.etapa?.ordem)
  const matchedIndex = FUNNEL_STAGES.findIndex((stage) => stage === (funnel?.currentStage ?? lead?.stage))
  const currentIndex = Number.isInteger(currentOrder) && currentOrder > 0 ? Math.min(currentOrder - 1, FUNNEL_STAGES.length - 1) : Math.max(0, matchedIndex)

  const nextStage = currentIndex >= 0 && currentIndex < FUNNEL_STAGES.length - 1
    ? FUNNEL_STAGES[currentIndex + 1]
    : '—'

  return (
    <Panel className={[styles.card, large ? styles.large : ''].filter(Boolean).join(' ')}>
      <PanelHead
        title="Funil de vendas"
        subtitle="Etapa atual e progresso da oportunidade"
        actions={large ? <Button onClick={onUpdateStage}>Atualizar etapa</Button> : null}
      />
      <div className={styles.stepper}>
        {FUNNEL_STAGES.map((stage, index) => {
          const state = index < currentIndex ? 'done' : index === currentIndex ? 'current' : 'todo'
          return (
            <div className={[styles.step, styles[state]].join(' ')} key={stage}>
              <span>{state === 'done' ? <Icon name="check" size={13} /> : index + 1}</span>
              <div>
                <strong>{stage}</strong>
                {state === 'current' && elapsed !== '—' && <small>{`Há ${elapsed} nesta etapa`}</small>}
              </div>
            </div>
          )
        })}
      </div>

      {large && (
        <div className={styles.meta}>
          {elapsed !== "—" && <Info
            icon="calendar"
            label="Entrada na etapa"
            value={stageEntryLabel(funnel?.enteredStageAt)}
          />}
          {elapsed !== "—" && <Info icon="clock" label="Tempo nesta etapa" value={elapsed} />}
          <Info icon="arrow" label="Próxima ação" value={nextStage} />
          <Info icon="user" label="Responsável" value={owner?.name ?? lead?.representativeName ?? '—'} />
        </div>
      )}
    </Panel>
  )
}

export default FunnelCard
