import { Badge } from '@/components/common/Badge'
import { Icon } from '@/components/common/Icon'
import { Panel, PanelHead } from '@/components/common/Panel'
import { Title } from '@/components/common/Title'
import { CADENCE_STATUS } from '@/models/enums'
import styles from './CadenceCard.module.css'

export function CadenceCard({ cadence, large = false }) {
  const status = CADENCE_STATUS[cadence?.status ?? 'ON_TRACK']

  return (
    <Panel className={[styles.card, large ? styles.large : ''].filter(Boolean).join(' ')}>
      <PanelHead title="Cadência" actions={<Badge tone={status.tone}>{status.label}</Badge>} />

      <div className={styles.meter}>
        <div>
          <span>Último contato</span>
          <strong>{cadence?.lastContactLabel ?? '—'}</strong>
        </div>
        <div className={styles.track}>
          <i style={{ width: `${cadence?.progress ?? 0}%` }} />
        </div>
        <div>
          <span>Próximo contato</span>
          <strong>{cadence?.nextContactLabel ?? '—'}</strong>
        </div>
      </div>

      {cadence?.nextAction && (
        <div className={styles.nextAction}>
          <span>
            <Icon name="calendar" />
          </span>
          <div>
            <small>PRÓXIMA ATIVIDADE</small>
            <strong>{cadence.nextAction.title}</strong>
            <p>{cadence.nextAction.whenLabel}</p>
          </div>
        </div>
      )}

      {large && cadence?.plan?.length > 0 && (
        <div className={styles.plan}>
          <Title level={3}>Plano de acompanhamento</Title>
          {cadence.plan.map((item, index) => (
            <div className={styles[item.state] ?? ''} key={item.title}>
              <span>{item.state === 'complete' ? <Icon name="check" size={12} /> : index + 1}</span>
              <strong>{item.title}</strong>
              <small>{item.whenLabel}</small>
            </div>
          ))}
        </div>
      )}
    </Panel>
  )
}

export default CadenceCard
