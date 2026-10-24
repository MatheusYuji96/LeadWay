import { useState } from 'react'
import { Button } from '@/components/common/Button'
import { Modal, SuccessContent } from '@/components/common/Modal'
import { SelectField, TextArea } from '@/components/common/Field'
import { companyInitials } from '@/models/lead'
import { mockRepresentatives } from '@/services/mock/fixtures'
import styles from './ActionModals.module.css'

export function TransferModal({ lead, saving, error, onClose, onConfirm }) {
  const [representativeId, setRepresentativeId] = useState('')
  const [reason, setReason] = useState('')
  const [done, setDone] = useState(false)

  const submit = async () => {
    const ok = await onConfirm({ representativeId, reason })
    if (ok) setDone(true)
  }

  if (done) {
    return (
      <Modal title="" onClose={onClose}>
        <SuccessContent
          title="Responsável atualizado"
          description={`A empresa ${lead?.name ?? ''} foi transferida com sucesso.`}
          actionLabel="Voltar para empresa"
          onAction={onClose}
        />
      </Modal>
    )
  }

  return (
    <Modal
      title="Alterar responsável"
      onClose={onClose}
      footer={
        <>
          <Button onClick={onClose}>Cancelar</Button>
          <Button kind="primary" onClick={submit} loading={saving}>
            Confirmar transferência
          </Button>
        </>
      }
    >
      <p className={styles.copy}>
        Transfira esta empresa e todo o seu histórico para outro representante.
      </p>

      <div className={styles.summary}>
        <span className={styles.summaryIcon}>{companyInitials(lead?.name ?? '')}</span>
        <div>
          <strong>{lead?.name ?? '—'}</strong>
          <span>Responsável atual: {lead?.representativeName ?? '—'}</span>
        </div>
      </div>

      <div className={styles.form}>
        <SelectField label="Novo responsável" value={representativeId} onChange={setRepresentativeId}>
          <option value="">Selecione um representante</option>
          {mockRepresentatives.map((rep) => (
            <option key={rep.id} value={rep.id}>
              {rep.name} — {rep.code}
            </option>
          ))}
        </SelectField>
        <TextArea
          label="Motivo da transferência"
          placeholder="Descreva brevemente o motivo..."
          value={reason}
          onChange={setReason}
        />
        {error && <p className={styles.error}>{error}</p>}
      </div>
    </Modal>
  )
}

export default TransferModal
