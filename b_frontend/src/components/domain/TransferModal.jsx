import { useEffect, useState } from 'react'
import { Button } from '@/components/common/Button'
import { Modal, SuccessContent } from '@/components/common/Modal'
import { SelectField, TextArea } from '@/components/common/Field'
import { companyInitials } from '@/models/lead'
import { representativesService } from '@/services/representatives.service'
import { http, unwrap, isMockMode } from '@/services/http'
import styles from './ActionModals.module.css'

export function TransferModal({ lead, saving, error, onClose, onConfirm }) {
  const [representativeId, setRepresentativeId] = useState('')
  const [reason, setReason] = useState('')
  const [done, setDone] = useState(false)
  const [representatives, setRepresentatives] = useState([])
  const [loadError, setLoadError] = useState('')
  useEffect(() => {
    let alive = true
    const fetch = isMockMode ? representativesService.list().then(r => r.items) : http.get('/vendedores').then(r => unwrap(r))
    fetch.then(rows => { if (alive) setRepresentatives(Array.isArray(rows) ? rows : []) })
      .catch(() => { if (alive) setLoadError('Não foi possível carregar os vendedores.') })
    return () => { alive = false }
  }, [])

  const submit = async () => {
    if (!representativeId) { setLoadError('Selecione um vendedor.'); return }
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
          {representatives.filter(rep => rep.ativo !== false && rep.active !== false && String(rep.id) !== String(lead?.representativeId)).map((rep) => (
            <option key={rep.id} value={rep.id}>
              {rep.nome ?? rep.name} — {rep.codVendedor ?? rep.code ?? ''}
            </option>
          ))}
        </SelectField>
        <TextArea
          label="Motivo da transferência"
          placeholder="Descreva brevemente o motivo..."
          value={reason}
          onChange={setReason}
        />
        {loadError && <p className={styles.error}>{loadError}</p>}
        {error && <p className={styles.error}>{error}</p>}
      </div>
    </Modal>
  )
}

export default TransferModal
