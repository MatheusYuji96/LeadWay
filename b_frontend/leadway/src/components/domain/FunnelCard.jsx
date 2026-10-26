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

export function FunnelCard({ funnel, lead, owner, large = false }) {
  const currentIndex = FUNNEL_STAGES.indexOf(funnel?.currentStage ?? lead?.stage ?? FUNNEL_STAGES[0])

  return (
    <Panel className={[styles.card, large ? styles.large : ''].filter(Boolean).join(' ')}>
      <PanelHead
        title="Funil de vendas"
        subtitle="Etapa atual e progresso da oportunidade"
        actions={large ? <Button>Atualizar etapa</Button> : null}
      />
      <div className={styles.stepper}>
        {FUNNEL_STAGES.map((stage, index) => {
          const state = index < currentIndex ? 'done' : index === currentIndex ? 'current' : 'todo'
          return (
            <div className={[styles.step, styles[state]].join(' ')} key={stage}>
              <span>{state === 'done' ? <Icon name="check" size={13} /> : index + 1}</span>
              <div>
                <strong>{stage}</strong>
                {state === 'current' && <small>Há {funnel?.daysInCurrentStage ?? 0} dias nesta etapa</small>}
              </div>
            </div>
          )
        })}
      </div>

      {large && (
        <div className={styles.meta}>
          <Info
            icon="calendar"
            label="Entrada na etapa"
            value={
              funnel?.enteredStageAt
                ? new Date(`${funnel.enteredStageAt}T00:00:00`).toLocaleDateString('pt-BR', {
                    day: 'numeric',
                    month: 'long',
                    year: 'numeric',
                  })
                : '—'
            }
          />
          <Info icon="clock" label="Tempo nesta etapa" value={`${funnel?.daysInCurrentStage ?? 0} dias`} />
          <Info icon="arrow" label="Próxima ação" value={lead?.nextAction ?? '—'} />
          <Info icon="user" label="Responsável" value={owner?.name ?? lead?.representativeName ?? '—'} />
        </div>
      )}
    </Panel>
  )
}

export default FunnelCard
