import { Avatar } from '@/components/common/Avatar'
import { Button } from '@/components/common/Button'
import { EmptyState } from '@/components/common/Feedback'
import { Icon } from '@/components/common/Icon'
import styles from './ContactList.module.css'

export function ContactList({ contacts = [], onAdd }) {
  return (
    <div className={styles.wrap}>
      <div className={styles.head}>
        <span />
        <span>Nome</span>
        <span>E-mail</span>
        <span>WhatsApp</span>
        <span />
      </div>

      {contacts.length === 0 && (
        <EmptyState
          icon="users"
          title="Nenhum contato vinculado"
          description="Adicione pessoas que participam da decisão de compra desta empresa."
        />
      )}

      {contacts.map((contact) => (
        <div className={styles.row} key={contact.id}>
          <Avatar initials={contact.initials} />
          <strong>{contact.name}</strong>
          <span>{contact.email}</span>
          <span>{contact.whatsapp}</span>
          <Button kind="ghost" aria-label={`Opções de ${contact.name}`}>
            <Icon name="more" size={16} />
          </Button>
        </div>
      ))}

      {onAdd && contacts.length > 0 && (
        <div className={styles.addRow}>
          <Button icon="plus" onClick={onAdd}>
            Adicionar contato
          </Button>
        </div>
      )}
    </div>
  )
}

export default ContactList
