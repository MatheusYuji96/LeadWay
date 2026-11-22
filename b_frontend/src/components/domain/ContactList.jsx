import { Avatar } from '@/components/common/Avatar'
import { Button } from '@/components/common/Button'
import { EmptyState } from '@/components/common/Feedback'
import { Icon } from '@/components/common/Icon'
import styles from './ContactList.module.css'

const formatPhone = (value) => {
  const d = String(value ?? '').replace(/\D/g, '')
  return d.length === 11 ? d.replace(/^(\d{2})(\d{5})(\d{4})$/, '($1) $2-$3') : value || '—'
}

export function ContactList({ contacts = [], onAdd, onEdit, onDelete }) {
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
          <span>{formatPhone(contact.whatsapp)}</span>
          <div className={styles.actions}>
            {onEdit && <button type="button" onClick={() => onEdit(contact)} title="Editar contato" aria-label={`Editar ${contact.name}`}><Icon name="edit" size={17} /></button>}
            {onDelete && <button type="button" onClick={() => onDelete(contact)} title="Excluir contato" aria-label={`Excluir ${contact.name}`}><Icon name="trash" size={17} /></button>}
          </div>
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
