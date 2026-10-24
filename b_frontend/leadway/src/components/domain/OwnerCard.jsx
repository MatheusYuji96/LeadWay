import { Avatar } from '@/components/common/Avatar'
import { Icon } from '@/components/common/Icon'
import { Panel, PanelHead } from '@/components/common/Panel'
import styles from './OwnerCard.module.css'

export function OwnerCard({ owner, lead }) {
  if (!owner) return null
  const joinedLabel = owner.since
    ? new Date(`${owner.since}T00:00:00`).toLocaleDateString('pt-BR', {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
      })
    : '—'

  return (
    <Panel>
      <PanelHead title="Responsável" actions={<Icon name="more" size={16} />} />
      <div className={styles.owner}>
        <Avatar initials={owner.initials} size="lg" />
        <div>
          <strong>{owner.name}</strong>
          <span>
            {owner.code} • {owner.region}
          </span>
        </div>
      </div>
      <div className={styles.meta}>
        <span>
          <small>Na carteira desde</small>
          <strong>{joinedLabel}</strong>
        </span>
        <span>
          <small>Leads ativos</small>
          <strong>{owner.leads ?? '—'}</strong>
        </span>
      </div>
      {lead?.representativeName && !owner.name && <p className={styles.fallback}>{lead.representativeName}</p>}
    </Panel>
  )
}

export default OwnerCard
