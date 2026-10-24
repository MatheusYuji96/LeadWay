import { useNavigate } from 'react-router-dom'
import { Avatar } from '@/components/common/Avatar'
import { Badge } from '@/components/common/Badge'
import { Button } from '@/components/common/Button'
import { EmptyState, ErrorState, Skeleton } from '@/components/common/Feedback'
import { Icon } from '@/components/common/Icon'
import { Title } from '@/components/common/Title'
import { contactStatusLabel, contactStatusTone } from '@/models/contact'
import { paths } from '@/routes/paths'
import styles from './ContactCards.module.css'

export function ContactCards({ contacts, loading, error, onRetry }) {
  const navigate = useNavigate()

  if (loading) return <Skeleton cards={2} lines={4} />
  if (error) return <ErrorState message={error} onRetry={onRetry} />
  if (contacts.length === 0) {
    return (
      <EmptyState
        icon="users"
        title="Nenhum contato encontrado"
        description="Ajuste a busca ou cadastre uma nova pessoa vinculada a uma empresa."
      />
    )
  }

  return (
    <div className={styles.grid}>
      {contacts.map((contact) => (
        <article className={styles.card} key={contact.id}>
          <div className={styles.top}>
            <Avatar initials={contact.initials} size="lg" />
            <Badge tone={contactStatusTone(contact.status)}>{contactStatusLabel(contact.status)}</Badge>
          </div>
          <Title level={3}>{contact.name}</Title>
          <strong className={styles.role}>{contact.role}</strong>
          <span className={styles.companyLine}>
            <Icon name="building" size={13} />
            {contact.companyName}
          </span>
          <div className={styles.data}>
            <span>
              <Icon name="mail" size={13} /> {contact.email}
            </span>
            <span>
              <Icon name="phone" size={13} /> {contact.whatsapp}
            </span>
          </div>
          <Button full onClick={() => navigate(`${paths.leads}/${contact.companyId}`)}>
            Ver histórico
          </Button>
        </article>
      ))}
    </div>
  )
}

export default ContactCards
